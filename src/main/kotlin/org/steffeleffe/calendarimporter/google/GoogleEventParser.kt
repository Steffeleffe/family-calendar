package org.steffeleffe.calendarimporter.google

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import org.slf4j.LoggerFactory
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.EventTimeRange
import org.steffeleffe.calendarservice.Participant
import org.steffeleffe.configurationservice.ConfigurationService
import java.util.*

class GoogleEventParser(private val configurationService: ConfigurationService) {

    private val logger = LoggerFactory.getLogger(GoogleEventParser::class.java)

    internal fun parseEvent(event: Event, calendarId: String): CalendarEvent? {
        val start: DateTime? = event.start.dateTime ?: event.start.date
        val end: DateTime? = event.end.dateTime ?: event.end.date

        if (start == null) {
            logger.warn("Ignoring event (id=${event.id},summary=${event.summary}) without start date.")
            return null
        }
        if (end == null) {
            logger.warn("Ignoring event (id=${event.id},summary=${event.summary}) without end date.")
            return null
        }
        if (event.summary == null) {
            logger.warn("Ignoring event (id=${event.id},start=$start,end=$end) without summary.")
            return null
        }
        return CalendarEvent(
                event.id,
                event.summary,
                EventTimeRange(Date(start.value), Date(end.value)),
                start.isDateOnly,
                getImageSource(event, calendarId),
                getParticipants(event, calendarId),
                calendarId
        )
    }

    private fun getParticipants(event: Event, calendarId: String): Set<Participant> {
        val regex = "[hH]vem:(.+)".toRegex()
        val find = regex.find(event.description ?: "")

        return when {
            find != null -> return when  {
                find.groupValues[1].equals("alle", ignoreCase = true) -> configurationService.participants.toSet()
                else -> find.groupValues[1].split(',')
                        .map { it.trim() }
                        .mapNotNull { getParticipantFromString(it) }
                        .toSet()
            }
            calendarId == "66aglhcacpcpupnhh9fian0a1g@group.calendar.google.com" -> {
                val participantFromString = getParticipantFromString("Rikke")
                if (participantFromString == null) emptySet() else setOf(participantFromString)
            }
            else -> emptySet()
        }
    }

    private fun getParticipantFromString(s: String): Participant? {
        val find = configurationService.participants.find { it.name.equals(s, ignoreCase = true) }
        if (find == null) {
            logger.warn("No participant in configuration matching string \"$s\"")
        }
        return find
    }

    private fun getImageSource(event: Event, calendarId: String): String? {
        val regex = "[bB]illede:(.+)".toRegex()
        val find = regex.find(event.description ?: "")
        return when {
            find != null -> trimAnchorTag(find.groupValues[1].trim())
            calendarId == "66aglhcacpcpupnhh9fian0a1g@group.calendar.google.com" -> "https://www.flaticon.com/svg/static/icons/svg/3209/3209008.svg"
            else -> null
        }
    }

    /**
     *  Removes any anchor href HTML tag from string
     */
    private fun trimAnchorTag(s: String): String {
        val regex = "<a href=.*>(.*)</a>".toRegex()
        val find = regex.find(s)
        return when {
            find != null -> find.groupValues[1]
            else -> s
        }
    }

}