package org.steffeleffe.calendarservice

import java.util.*

interface CalendarService {
    fun getAllCalendars(): List<CalendarEvent>
    fun refresh()
}

data class CalendarEvent(
        val id: String,
        val description: String,
        val timeRange: EventTimeRange,
        val allDayEvent: Boolean = false,
        val imageSource: String? = null,
        val participants: Set<Participant> = emptySet(),
        val calendarId : String) {
    var startTimeSlot: String = ""
    var endTimeSlot: String = ""
}

data class Participant(val name: String, val abbreviation: Char)

data class EventTimeRange(val start: Date, val end: Date)
