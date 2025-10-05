package ad.julian.elytra.protocol.types

data class ServerTemplate @JvmOverloads constructor(
    var name: String = "",
    var dir: String = "",
    var image: String = "itzg/minecraft-server",
    var type: String = "PAPER",
    var version: String = "LATEST",
    var port: Int = 25565,
    var memory: Int? = null,
    var environment: Map<String, String>? = emptyMap(),
    var plugins: List<String>? = emptyList(),
    var files: Map<String, String>? = emptyMap(),
    var network: String? = null
)