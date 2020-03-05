package org.steffeleffe.calendarservice

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.util.concurrent.ListenableFutureTask
import org.steffeleffe.calendarimport.GoogleCalendarImporter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
open class CalendarEventCache(val googleCalendarImporter: GoogleCalendarImporter) {

    private val numberOfDays : Int = 5

    inner class CalendarEventCacheLoader : CacheLoader<String, List<CalendarEvent>>() {
        override fun load(key: String): List<CalendarEvent> { // no checked exception
            return getEvents(key)
        }

        override fun reload(key: String, oldValue: List<CalendarEvent>): ListenableFutureTask<List<CalendarEvent>> {
            val task: ListenableFutureTask<List<CalendarEvent>> = ListenableFutureTask.create { getEvents(key) }
            Executors.newSingleThreadExecutor().execute(task)
            return task
        }

        private fun getEvents(key: String): List<CalendarEvent> = googleCalendarImporter.importCalender(key, numberOfDays)

    }

    private val cache: LoadingCache<String, List<CalendarEvent>> = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(CalendarEventCacheLoader())

    fun get(key: String): List<CalendarEvent> = cache[key]

    fun invalidateAll() = cache.invalidateAll()

}