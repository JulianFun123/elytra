package ad.julian.elytra.protocol.types

data class ScaleRule(
    var cpuThreshold: Double? = null,
    var memoryThreshold: Double? = null,
    var playerCount: Int? = null,
    var script: String? = null,
)

data class ScalingConfig(
    var min: Int = 1,
    var max: Int = 5,
    var upscale: MutableList<ScaleRule> = mutableListOf(),
    var downscale: MutableList<ScaleRule> = mutableListOf(),
    var checkIntervalSeconds: Int = 5,
)


data class LifecycleConfig(
    var scaling: ScalingConfig? = null,
    var autostart: Boolean = false,
)

enum class ServerGroupType {
    STATIC,
    DYNAMIC
}

data class ServerGroup(
    var name: String = "",
    var templateName: String = "",
    var type: ServerGroupType = ServerGroupType.DYNAMIC,
    var lifecycle: LifecycleConfig? = null,
    var lobby: Boolean = false,
    var labels: Map<String, String>? = emptyMap(),
    var environment: Map<String, String>? = emptyMap(),
    var dir: String? = "",

    var memory: Int? = null,
    var network: String? = null

) {
    init {
        if (type == ServerGroupType.STATIC) {
            if (lifecycle?.scaling != null) {
                throw IllegalArgumentException("Scaling config cannot be applied to static server groups")
            }
            if (lifecycle?.autostart == true) {
                throw IllegalArgumentException("Autostart cannot be applied to static server groups")
            }
        } else if (type == ServerGroupType.DYNAMIC) {
            if (dir != null && dir!!.isNotEmpty()) {
                throw IllegalArgumentException("Dir cannot be applied to dynamic server groups. Dir is auto-generated per instance. Set it in the template.")
            }
        }
    }
}