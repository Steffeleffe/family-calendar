package org.steffeleffe

import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.CalendarService
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/calendar")
open class CalendarResource(val calendarService: CalendarService){

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllCalendars(): List<CalendarEvent> {
        return calendarService.getAllCalendars()
    }

    @Path("/refresh")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun refreshCalenders(): String {
        calendarService.refresh()
        return "Caches are refreshed"
    }

}