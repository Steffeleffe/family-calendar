package org.steffeleffe

import kotlinx.css.*
import kotlinx.css.Float
import kotlinx.css.properties.border
import org.eclipse.microprofile.metrics.MetricUnits
import org.eclipse.microprofile.metrics.annotation.Timed
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.CalendarService
import org.steffeleffe.configurationservice.ConfigurationService
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/style.css")
open class CssResource(val calendarService: CalendarService, val configurationService: ConfigurationService) {

    @GET
    @Timed(name = "timed", description = "A measure of how long it takes to fetch css page.", unit = MetricUnits.MILLISECONDS)
    @Produces("text/css")
    fun hello(): String {
        val allCalendars = calendarService.getAllCalendars()
        val usedTimes = getAllTimesUsedInEvents(allCalendars)

        val cssBuilder = CSSBuilder().apply {
            body {
                backgroundColor = Color.darkSlateGrey
            }
            rule(".calendar") {
                display = Display.grid
                var gridRowDefinitions = "[dayHeader] auto\n"
                usedTimes.sorted().forEach { time ->
                    gridRowDefinitions += "[time$time] auto\n"
                }

                put("grid-template-rows", gridRowDefinitions)

                var gridColumnDefinitions = if (displayTimeSlots) "[timeSlot] auto\n" else ""
                for (day in 0 until 5) {
                    gridColumnDefinitions += "[day$day] 1fr\n 2em \n"
                }
                put("grid-template-columns", gridColumnDefinitions)
                put("grid-gap", "0.1em")
            }


            usedTimes.forEach { time ->
                rule(".timeSlot$time") {
                    put("grid-row", "time$time")
                    put("grid-column", "timeSlot")
                }
            }

            for (day in 0 until 5) {
                rule(".day$day") {
                    put("grid-row", "dayHeader")
                    put("grid-column", "day$day")
                }
            }

            allCalendars.forEach { event ->

                val c = Calendar.getInstance()
                val today = c.get(Calendar.DAY_OF_YEAR)

                c.time = event.timeRange.start
                val daysFromNow = c.get(Calendar.DAY_OF_YEAR) - today
                val startTime = c.getPaddedTimeOfDay()
                c.time = event.timeRange.end
                val endTime = c.getPaddedTimeOfDay()

                rule(".event${event.id}") {
                    if (verticalEventSpace) {
                        put("grid-row", "time$startTime / time$endTime")
                    } else {
                        put("grid-row", "time$startTime")
                    }
                    put("grid-column", "day$daysFromNow")
                    backgroundColor = randomColor()
                }
            }
            rule(".event") {
                borderStyle = BorderStyle.solid
                padding = "0.5em"
            }
            rule(".day") {
                fontWeight = FontWeight.bold
                fontSize = LinearDimension("larger")
                fontFamily = "sans-serif"
                textAlign = TextAlign.center
            }
            rule(".eventTime") {
                fontSize = LinearDimension("small")
            }
            rule(".eventDescription") {
                fontWeight = FontWeight.bold
            }
            rule(".eventImage") {
                maxWidth = LinearDimension("3em")
                maxHeight = LinearDimension("3em")
                float = Float.right
            }

            rule(".eventParticipants") {
                display = Display.flex
            }

            rule(".eventParticipant") {
                fontFamily = "sans-serif"
                border(LinearDimension("2pt"), BorderStyle.solid, Color.white, LinearDimension("50%"))
                width = LinearDimension("1em")
                height = LinearDimension("1em")
                padding = ".5em"
                textAlign = TextAlign.center
                fontSize = LinearDimension("10pt")
                fontWeight = FontWeight.bold
            }

            configurationService.getColorConfigurations().forEach {
                rule(".participant"+it.participant.name) {
                    backgroundColor = it.color
                }
            }
        }
        return cssBuilder.toString()
    }





    private fun randomColor(): Color {
        val random = Random()
        val rgb = "#${random.nextInt(5)+5}${random.nextInt(5)+5}${random.nextInt(5)+5}"
        return Color(rgb)
    }

    private fun getAllTimesUsedInEvents(allCalendars: List<CalendarEvent>): MutableSet<String> {
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
        return usedTimes
    }

}