package org.steffeleffe.calendarservice

import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
open class CalendarService() {

    private val cache = CalenderEventCache().cache

    fun getAllCalendars(): List<CalendarEvent> {

        val joinedList = mutableListOf<CalendarEvent>()

        joinedList.addAll(cache.get("primary"))
        joinedList.addAll(cache.get("3eq4uqnkhcgipgkdrrhs7ec6e4@group.calendar.google.com"))
        joinedList.addAll(cache.get("rikke.vangsted@gmail.com"))
        joinedList.addAll(cache.get("66aglhcacpcpupnhh9fian0a1g@group.calendar.google.com"))
        joinedList.addAll(cache.get("hdn3t11kjru1fs823pee8g9bso@group.calendar.google.com"))

        return joinedList
    }

}

