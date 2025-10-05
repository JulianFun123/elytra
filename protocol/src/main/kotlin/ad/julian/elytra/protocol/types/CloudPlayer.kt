package ad.julian.elytra.protocol.types

data class CloudPlayer(
    val proxy: String?,
    val userName: String,
    val uuid: String,
    val currentServerId: String?,
    val ping: Long,
    val onlineMode: Boolean
)