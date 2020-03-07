package org.steffeleffe

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Timed
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.CalendarService
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/index.html")
open class HtmlResource (val calendarService: CalendarService){

    @GET
    @Timed(description = "A measure of how long it takes to fetch html page.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_HTML)
    fun hello(): String {
        val allCalendars = calendarService.getAllCalendars()
        val createHTML = createHTML(true, true)
        createHTML.head {
            styleLink("/style.css")
            meta {
                httpEquiv = "refresh"
                content = "30"
            }
            meta("google", "notranslate")
        }
        createHTML.body {
            div(classes = "calendar") {

                val usedTimes = mutableSetOf<String>()

                allCalendars.forEach { event ->
                    val c = Calendar.getInstance()
                    c.time = event.timeRange.start
                    usedTimes.add(c.getPaddedTimeOfDay())
                    if (verticalEventSpace) {
                        c.time = event.timeRange.end
                        usedTimes.add(c.getPaddedTimeOfDay())
                    }
                }
                if (displayTimeSlots) {
                    usedTimes.forEach { time ->
                        div(classes = "timeSlot$time") { +time }
                    }
                }

                for (day in 0 until 5) {
                    val c = Calendar.getInstance()
                    c.time = Date()
                    c.add(Calendar.DAY_OF_YEAR, day)
                    val dayDisplay = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("da", "DK", "DK"))
                    div(classes = "day day$day") { +dayDisplay }
                }

                allCalendars.forEach { event ->
                    div(classes = "event event${event.id}") {
                        getImage(event)
                        div(classes = "eventTime") {
                            val c = Calendar.getInstance()
                            c.time = event.timeRange.start
                            val startTime = c.getPaddedDisplayTimeOfDay()
                            c.time = event.timeRange.end
                            val endTime = c.getPaddedDisplayTimeOfDay()
                            +"$startTime - $endTime"
                        }
                        div(classes = "eventDescription") {
                            +event.description
                        }
                        if (event.participants.isNotEmpty()) {
                            div(classes = "eventParticipants") {
                                for (participant in event.participants) {
                                    div(classes = "eventParticipant participant${participant.name}") {
                                        +participant.abbreviation.toString()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        return createHTML.finalize()
    }

    private fun DIV.getImage(event: CalendarEvent) {
        if (event.imageSource != null) {
            if (event.imageSource.startsWith("http")) {
                img(classes = "eventImage") {
                    src = event.imageSource
                }
            } else {
                img(classes = "eventImage") {
                    src = "/images/" + event.imageSource + ".svg"
                }
            }

        }
    }



}