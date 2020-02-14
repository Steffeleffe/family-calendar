package org.steffeleffe

import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.CalendarService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/calendar")
class CalendarResource {

    @Inject
    lateinit var calendarService: CalendarService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllCalendars(): List<CalendarEvent> {
        return calendarService.getAllCalendars()
    }

}