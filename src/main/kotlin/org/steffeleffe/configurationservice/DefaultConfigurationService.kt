package org.steffeleffe.configurationservice

import kotlinx.css.Color
import org.steffeleffe.calendarservice.Participant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
open class DefaultConfigurationService : ConfigurationService {

    private final val participantConfigurations = setOf(
            ParticipantConfiguration(name = "Rikke", color = Color.darkSeaGreen),
            ParticipantConfiguration(name = "Steffen", color = Color.cornflowerBlue),
            ParticipantConfiguration(name = "Ada", color = Color.hotPink),
            ParticipantConfiguration(name = "Ebbe", color = Color.sandyBrown),
            ParticipantConfiguration(name = "Marie", color = Color.paleVioletRed)
    )

    override val googleCalendarIds = listOf(
            "primary",
            "3eq4uqnkhcgipgkdrrhs7ec6e4@group.calendar.google.com",
            "rikke.vangsted@gmail.com",
            "66aglhcacpcpupnhh9fian0a1g@group.calendar.google.com",
            "hdn3t11kjru1fs823pee8g9bso@group.calendar.google.com"
    )

    override val participants = participantConfigurations.map { it.getParticipant() }

    override val colorConfigurations = participantConfigurations.map { ColorConfiguration(it.getParticipant(), it.color) }

    data class ParticipantConfiguration(
            val name: String,
            val abbreviation: Char = name[0],
            val color: Color) {
        fun getParticipant(): Participant {
            return Participant(name, abbreviation)
        }
    }

}


