package ad.julian.elytra.core.config

import ad.julian.elytra.protocol.types.ServerTemplate
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
class TemplatesConfig : YamlConfig(File("templates.yml")) {
    var templates: MutableList<ServerTemplate> = mutableListOf(
        ServerTemplate(
            name = "example",
            dir = "templates/example",
            environment = mapOf(
                "MOTD" to "A Minecraft Server powered by Inxane Cloud",
                "MAX_PLAYERS" to "20"
            ),
        )
    )
}