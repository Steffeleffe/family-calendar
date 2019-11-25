package org.steffeleffe.calendarservice

import java.util.*

data class CalendarEvent(val id: String,
                         val description : String,
                         val timeRange: EventTimeRange,
                         val allDayEvent: Boolean = false,
                         val imageSource: String? = null,
                         val participants: Set<Participant> = emptySet())


data class Participant(val name: String, val abbreviation: Char)

data class EventTimeRange(val start: Date,
                          val end: Date)