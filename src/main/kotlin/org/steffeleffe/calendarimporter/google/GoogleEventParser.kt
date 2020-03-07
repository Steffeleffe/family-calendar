package org.steffeleffe.calendarimporter.google

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import org.slf4j.LoggerFactory
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.EventTimeRange
import org.steffeleffe.calendarservice.Participant
import org.steffeleffe.configurationservice.ConfigurationService
import java.util.*
import javax.enterprise.context.ApplicationScoped

class GoogleEventParser(private val configurationService: ConfigurationService) {

    private val logger = LoggerFactory.getLogger(GoogleEventParser::class.java)

    internal fun parseEvent(event: Event): CalendarEvent? {
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
                getImageSource(event),
                getParticipants(event)
        )
    }

    private fun getParticipants(event: Event): Set<Participant> {
        val regex = "[hH]vem:(.+)".toRegex()
        val find = regex.find(event.description ?: "")
        return when  {
            find == null -> emptySet()
            find.groupValues[1].equals("alle", ignoreCase = true) -> configurationService.participants.toSet()
            else -> find.groupValues[1].split(',')
                    .map { it.trim() }
                    .mapNotNull { getParticipantFromString(it) }
                    .toSet()
        }
    }

    private fun getParticipantFromString(s: String): Participant? {
        val find = configurationService.participants.find { it.name.equals(s, ignoreCase = true) }
        if (find == null) {
            logger.warn("No participant in configuration matching string \"$s\"")
        }
        return find
    }

    private fun getImageSource(event: Event): String? {
        val regex = "[bB]illede:(.+)".toRegex()
        val find = regex.find(event.description ?: "")
        return when {
            find != null -> trimAnchorTag(find.groupValues[1].trim())
            event.summary.contains("vagt", ignoreCase = true) -> "sygeplejerske"
            event.summary.contains("kontordag", ignoreCase = true) -> "sygeplejerske"
            event.summary.contains("spise", ignoreCase = true) -> "spise"
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