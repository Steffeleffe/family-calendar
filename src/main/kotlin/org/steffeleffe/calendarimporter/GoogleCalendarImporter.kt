package org.steffeleffe.calendarimporter

import com.google.api.services.calendar.model.Events
import org.steffeleffe.calendarimporter.google.GoogleCalendarImporterFetcher
import org.steffeleffe.calendarimporter.google.GoogleEventParser
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.configurationservice.ConfigurationService
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
open class GoogleCalendarImporter(configurationService: ConfigurationService) : CalendarImporter {

    val importerFetcher = GoogleCalendarImporterFetcher()

    val eventParser = GoogleEventParser(configurationService)

    override fun importCalender(calendarId: String, numberOfDaysToImport: Int): List<CalendarEvent> {
        val events: Events = importerFetcher.fetchEvents(numberOfDaysToImport, calendarId)

        return events.items
                .mapNotNull { eventParser.parseEvent(it, calendarId) }
                .filter { eventIsTodayOrLater(it) }
                .toList()
    }

    private fun eventIsTodayOrLater(it: CalendarEvent): Boolean {
        val c = Calendar.getInstance()
        val today = c.get(Calendar.DAY_OF_YEAR)
        c.time = it.timeRange.start
        return c.get(Calendar.DAY_OF_YEAR) >= today
    }

}