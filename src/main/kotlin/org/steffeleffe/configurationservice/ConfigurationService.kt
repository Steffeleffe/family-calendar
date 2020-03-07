package org.steffeleffe.configurationservice

import kotlinx.css.Color
import org.steffeleffe.calendarservice.Participant

interface ConfigurationService {
    val participants: List<Participant>
    val colorConfigurations: List<ColorConfiguration>
    val googleCalendarIds: List<String>
}

data class ColorConfiguration(val participant: Participant, val color : Color)