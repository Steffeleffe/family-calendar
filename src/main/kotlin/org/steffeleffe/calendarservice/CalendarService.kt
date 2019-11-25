package org.steffeleffe.calendarservice

import org.steffeleffe.calendarimport.GoogleCalendarImporter
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject

@ApplicationScoped
open class CalendarService {

    private val googleCalendarImporter = GoogleCalendarImporter()

    fun getAllCalendars(numberOfDays: Int): List<CalendarEvent> {

        val joinedList = mutableListOf<CalendarEvent>()

        joinedList.addAll(googleCalendarImporter.importCalender("primary", numberOfDays))
        joinedList.addAll(googleCalendarImporter.importCalender("3eq4uqnkhcgipgkdrrhs7ec6e4@group.calendar.google.com", numberOfDays))
        joinedList.addAll(googleCalendarImporter.importCalender("rikke.vangsted@gmail.com", numberOfDays))
        joinedList.addAll(googleCalendarImporter.importCalender("66aglhcacpcpupnhh9fian0a1g@group.calendar.google.com", numberOfDays))
        joinedList.addAll(googleCalendarImporter.importCalender("hdn3t11kjru1fs823pee8g9bso@group.calendar.google.com", numberOfDays))

        return joinedList
    }

}

