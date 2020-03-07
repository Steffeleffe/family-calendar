package org.steffeleffe.calendarservice

import org.steffeleffe.calendarservice.cached.CalendarEventCache
import org.steffeleffe.configurationservice.DefaultConfigurationService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
open class CachedCalendarService(val cache: CalendarEventCache,
                                 val configurationService: DefaultConfigurationService) : CalendarService {

    override fun getAllCalendars(): List<CalendarEvent> {
        return configurationService.googleCalendarIds
                .map { cache.get(it) }
                .flatten()
    }

    override fun refresh() {
        cache.invalidateAll()
        getAllCalendars()
    }

}

