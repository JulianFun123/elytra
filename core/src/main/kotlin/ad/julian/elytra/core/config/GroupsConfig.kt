package ad.julian.elytra.core.config

import ad.julian.elytra.protocol.types.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
class GroupsConfig : YamlConfig(File("groups.yml")) {
    var groups: MutableList<ServerGroup> = mutableListOf(
        ServerGroup(
            name = "example",
            type = ServerGroupType.DYNAMIC,
            templateName = "example",
            lifecycle = LifecycleConfig(
                ScalingConfig(
                    min = 1,
                    max = 3,
                    checkIntervalSeconds = 5,
                    upscale = mutableListOf(
                        ScaleRule(playerCount = 2)
                    ),
                )
            ),
        )
    )
}