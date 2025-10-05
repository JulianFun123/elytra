package ad.julian.elytra.protocol.types

data class Proxy(
    var name: String = "",
    var templateName: String = "",
    var autostart: Boolean? = null,
    var port: Int? = null,
    var portMapping: MutableList<String> = mutableListOf(),
    var groups: MutableList<String> = mutableListOf(),
) {
    init {
    }
}