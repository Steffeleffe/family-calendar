package org.steffeleffe.calendarimporter.google

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
import com.google.api.services.calendar.model.Events
import org.slf4j.LoggerFactory
import org.steffeleffe.calendarimporter.GoogleCalendarImporter
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class GoogleCalendarImporterFetcher {

    private val logger = LoggerFactory.getLogger(GoogleCalendarImporter::class.java)

    fun fetchEvents(numberOfDaysToImport: Int, calendarId: String): Events {
        // Build a new authorized API client service.
        val events: Events
        try {
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

            logger.info("Fetching events from Google Calendar with id=$calendarId")
            events = service.events().list(calendarId)
                    .setMaxResults(1000)
                    .setTimeMin(DateTime(todayTrimmedToDay.toEpochMilli()))
                    .setTimeMax(DateTime(futureTrimmedToDay.toEpochMilli()))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
        } catch (e: Exception) {
            throw RuntimeException("Error fetching events from Google", e)
        }
        return events
    }

    companion object {
        private const val APPLICATION_NAME = "org.steffeleffe.familycalendar"
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
        private const val TOKENS_DIRECTORY_PATH = "/home/steffen/GitHub/family-calendar/src/main/resources/tokens"

        /**
         * Global instance of the scopes required by this quickstart.
         * If modifying these scopes, delete your previously saved tokens/ folder.
         */
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
        private const val CREDENTIALS_FILE_PATH = "/credentials.json"

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

}

