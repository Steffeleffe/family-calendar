package org.steffeleffe

import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Timed
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.CalendarService
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/calendar")
open class CalendarResource(val calendarService: CalendarService){

    @GET
    @Timed(description = "A measure of how long it takes to get all calendar events.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllCalendars(): List<CalendarEvent> {
        return calendarService.getAllCalendars()
    }

    @Path("/refresh")
    @GET
    @Timed(description = "A measure of how long it takes to refresh calendars.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
    fun refreshCalenders(): String {
        calendarService.refresh()
        return "Caches are refreshed"
    }

}