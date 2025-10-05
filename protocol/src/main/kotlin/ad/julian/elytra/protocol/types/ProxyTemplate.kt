package ad.julian.elytra.protocol.types

enum class ProxyType {
    VELOCITY,
    BUNGEECORD
}

data class ProxyTemplate(
    var name: String = "",
    var dir: String = "",
    var image: String = "itzg/mc-proxy",
    var type: ProxyType = ProxyType.VELOCITY,
    var version: String = "LATEST",
    var port: Int = 25565,
    var memory: Int? = null,
    var environment: Map<String, String>? = emptyMap(),
    var groups: MutableList<String> = mutableListOf(),
    var config: MutableMap<String, String?> = mutableMapOf(),
    var plugins: List<String>? = emptyList(),
    var files: Map<String, String>? = emptyMap(),
    var network: String = "bridge"
)