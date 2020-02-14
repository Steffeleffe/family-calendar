package org.steffeleffe.calendarservice

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.util.concurrent.ListenableFutureTask
import org.steffeleffe.calendarimport.GoogleCalendarImporter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class CalenderEventCache {

    private val numberOfDays = 5

    private val googleCalendarImporter = GoogleCalendarImporter()

    inner class CalendarEventCacheLoader : CacheLoader<String, List<CalendarEvent>>() {
        override fun load(key: String): List<CalendarEvent> { // no checked exception
            return getEvents(key)
        }

        override fun reload(key: String, oldValue: List<CalendarEvent>): ListenableFutureTask<List<CalendarEvent>> {
            val task: ListenableFutureTask<List<CalendarEvent>> = ListenableFutureTask.create { getEvents(key) }
            Executors.newSingleThreadExecutor().execute(task)
            return task
        }

        private fun getEvents(key: String) : List<CalendarEvent> = googleCalendarImporter.importCalender(key, numberOfDays)

    }

    val cache: LoadingCache<String, List<CalendarEvent>> = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(CalendarEventCacheLoader())


}