package org.steffeleffe

import kotlinx.css.*
import kotlinx.css.Float
import kotlinx.css.properties.border
import org.steffeleffe.calendarservice.CalendarEvent
import org.steffeleffe.calendarservice.CalendarService
import org.steffeleffe.configurationservice.DefaultConfigurationService
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/style.css")
open class CssResource(val calendarService: CalendarService, val configurationService: DefaultConfigurationService) {

    @GET
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

                rule(".event${event.id}") {
                    if (verticalEventSpace) {
                        put("grid-row", "${event.startTimeSlot} / ${event.endTimeSlot}")
                    } else {
                        put("grid-row", "${event.startTimeSlot}")
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
                color = Color.white
            }
            rule(".eventTime") {
                fontSize = LinearDimension("small")
                fontFamily = "sans-serif"
            }
            rule(".eventDescription") {
                fontWeight = FontWeight.bold
                color = Color.white
                fontFamily = "sans-serif"
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

            configurationService.colorConfigurations.forEach {
                rule(".participant"+it.participant.name) {
                    backgroundColor = it.color
                }
            }
        }
        return cssBuilder.toString()
    }





    private fun randomColor(): Color {
        val random = Random()
        val rgb = "#${random.nextInt(3)+5}${random.nextInt(3)+5}${random.nextInt(3)+5}"
        return Color(rgb)
    }

    private fun getAllTimesUsedInEvents(allCalendars: List<CalendarEvent>): MutableSet<String> {
        val usedTimes = mutableSetOf<String>()

        val groupByDay = allCalendars.groupBy {
            val c = Calendar.getInstance()
            c.time = it.timeRange.start
            c.get(Calendar.DATE)
        }

        groupByDay.forEach {
            val groupByTimeByDay = it.value.groupBy {
                val c = Calendar.getInstance()
                c.time = it.timeRange.start
                c.getPaddedTimeOfDay()
            }
            groupByTimeByDay.forEach {
                for (i in 0 until it.value.size) {

                    val timeslot = "${it.key}_$i"
                    usedTimes.add(timeslot)
                    it.value.get(i).startTimeSlot = "time"+timeslot

                }
            }
        }

        allCalendars.forEach { event ->
            val c = Calendar.getInstance()
            c.time = event.timeRange.start
            val paddedStartTime = c.getPaddedTimeOfDay()


            //  usedTimes.add(c.getPaddedTimeOfDay()+event.id)
            if (verticalEventSpace) {
                c.time = event.timeRange.end
                var paddedEndTime = c.getPaddedTimeOfDay()
                val time = if (paddedEndTime < paddedStartTime) "2400" else paddedEndTime

                usedTimes.add(time)
                event.endTimeSlot = "time" + time
            }
        }
        return usedTimes
    }

}