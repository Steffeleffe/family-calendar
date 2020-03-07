package org.steffeleffe.calendarimporter

import org.steffeleffe.calendarservice.CalendarEvent

interface CalendarImporter {
    fun importCalender(calendarId: String, numberOfDaysToImport: Int): List<CalendarEvent>
}


