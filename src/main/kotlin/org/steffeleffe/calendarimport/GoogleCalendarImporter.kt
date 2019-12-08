package org.steffeleffe.calendarimport

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import org.slf4j.LoggerFactory
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.EventTimeRange
import org.steffeleffe.calendarservice.Participant
import org.steffeleffe.configurationservice.ConfigurationService
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
open class GoogleCalendarImporter {

    private val LOGGER = LoggerFactory.getLogger("GoogleCalendarImporter")

    private fun getEvents(calendarId: String, numberOfDaysToImport: Int) : List<CalendarEvent> {
        // Build a new authorized API client service.
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val service = com.google.api.services.calendar.Calendar.Builder(
            httpTransport,
            JSON_FACTORY,
            getCredentials(httpTransport)
        )
            .setApplicationName(APPLICATION_NAME)
            .build()

        val todayTrimmedToDay = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val futureTrimmedToDay = todayTrimmedToDay.plus(numberOfDaysToImport.toLong(), ChronoUnit.DAYS)

        println("Fetching events from Google Calendar with id=$calendarId")
        val events = service.events().list(calendarId)
            .setMaxResults(1000)
            .setTimeMin(DateTime(todayTrimmedToDay.toEpochMilli()))
            .setTimeMax(DateTime(futureTrimmedToDay.toEpochMilli()))
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        return events.items
                .mapNotNull { parseEvent(it) }
                .filter { eventIsTodayOrLater(it) }
                .toList()
    }

    private fun eventIsTodayOrLater(it: CalendarEvent): Boolean {
        val c = Calendar.getInstance()
        val today = c.get(Calendar.DAY_OF_YEAR)
        c.time = it.timeRange.start
        return c.get(Calendar.DAY_OF_YEAR) >= today
    }

    private fun parseEvent(event: Event): CalendarEvent? {
        val start: DateTime? = event.start.dateTime ?: event.start.date
        val end: DateTime? = event.end.dateTime ?: event.end.date

        if (start == null) {
            LOGGER.warn("Ignoring event (id=${event.id},summary=${event.summary}) without start date.")
            return null
        }
        if (end == null) {
            LOGGER.warn("Ignoring event (id=${event.id},summary=${event.summary}) without end date.")
            return null
        }
        if (event.summary == null) {
            LOGGER.warn("Ignoring event (id=${event.id},start=$start,end=$end) without summary.")
            return null
        }
        return CalendarEvent(
            event.id,
            event.summary,
            EventTimeRange(Date(start.value), Date(end.value)),
            start.isDateOnly,
            getImageSource(event),
            getParticipants(event)
        )
    }

    private fun getParticipants(event: Event): Set<Participant> {
        val regex = "[hH]vem:(.+)".toRegex()
        val find = regex.find(event.description ?: "")
        return when  {
            find == null -> emptySet()
            find.groupValues[1].equals("alle", ignoreCase = true) -> ConfigurationService().participants
            else -> find.groupValues[1].split(',')
                    .map { it.trim() }
                    .mapNotNull { getParticipantFromString(it) }
                    .toSet()
        }
    }

    private fun getParticipantFromString(s: String): Participant? {
        return ConfigurationService().getParticipantIgnoreCase(s)
    }

    private fun getImageSource(event: Event): String? {
        val regex = "[bB]illede:(.+)".toRegex()
        val find = regex.find(event.description ?: "")
        return when {
            find != null -> find.groupValues[1]
            event.summary.contains("vagt", ignoreCase = true) -> "sygeplejerske"
            event.summary.contains("kontordag", ignoreCase = true) -> "sygeplejerske"
            event.summary.contains("spise", ignoreCase = true) -> "spise"
            else -> null
        }
    }

    companion object {
        private val APPLICATION_NAME = "org.steffeleffe.familycalendar"
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
        private val TOKENS_DIRECTORY_PATH = "tokens"

        /**
         * Global instance of the scopes required by this quickstart.
         * If modifying these scopes, delete your previously saved tokens/ folder.
         */
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
        private val CREDENTIALS_FILE_PATH = "/credentials.json"

        /**
         * Creates an authorized Credential object.
         * @param HTTP_TRANSPORT The network HTTP Transport.
         * @return An authorized Credential object.
         * @throws IOException If the credentials.json file cannot be found.
         */
        @Throws(IOException::class)
        private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
            // Load client secrets.
            val inputStream = GoogleCalendarImporter::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
                ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

            // Build flow and trigger user authorization request.
            val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
            )
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
            val receiver = LocalServerReceiver.Builder().setPort(8888).build()
            return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        }
    }

    fun importCalender(calendarId: String, numberOfDaysToImport: Int): List<CalendarEvent> {
        return getEvents(calendarId, numberOfDaysToImport)
    }

}

