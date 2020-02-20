package org.steffeleffe.configurationservice

import kotlinx.css.Color
import org.slf4j.LoggerFactory
import org.steffeleffe.calendarservice.Participant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ConfigurationService {

    private val LOGGER = LoggerFactory.getLogger("ConfigurationService")

     val participants = setOf(
            Participant("Rikke", 'R'),
            Participant("Steffen", 'S'),
            Participant("Ada", 'A'),
            Participant("Ebbe", 'E'),
            Participant("Marie", 'M')
    )

    private val colorConfigurations : MutableSet<ColorConfiguration> = mutableSetOf(
            ColorConfiguration(getParticipantIgnoreCase("Rikke")!!, Color.darkSeaGreen),
            ColorConfiguration(getParticipantIgnoreCase("Steffen")!!, Color.sandyBrown),
            ColorConfiguration(getParticipantIgnoreCase("Ada")!!, Color.hotPink),
            ColorConfiguration(getParticipantIgnoreCase("Ebbe")!!, Color.cornflowerBlue),
            ColorConfiguration(getParticipantIgnoreCase("Marie")!!, Color.mediumVioletRed)
    )

    fun getColorConfigurations() : Set<ColorConfiguration> = colorConfigurations

    final fun getParticipantIgnoreCase(s: String): Participant? {
        val find = participants.find { it.name.equals(s, ignoreCase = true) }
        if (find == null) {
            LOGGER.warn("No participant in configuration matching string \"$s\"")
        }
        return find
    }

}


