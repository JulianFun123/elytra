package ad.julian.elytra.protocol.types

import java.util.*

data class ServerInfo(
    val proxyPlayerCount: Int = -1,
    val proxyPlayers: List<String> = mutableListOf(),
    val pingPlayerCount: Int = -1,
    val pingMaxPlayerCount: Int = -1,
    val pingSamplePlayers: MutableMap<UUID, String> = mutableMapOf(),
    val pingDescription: String = "",
    val pingTime: Long = -1,
    val pingVersionName: String = "",
    val pingVersionProtocol: Int = -1,
)

data class RunningServer(
    var id: String,
    var group: ServerGroup,
    var status: ServerStatus,
    var containerId: String,
    var creationTime: Long = System.currentTimeMillis(),
    var lastStatusChange: Long = System.currentTimeMillis(),
    var nodeId: String = "master",
    var labels: Map<String, String>? = mutableMapOf(),
    var info: ServerInfo = ServerInfo(),
) {
    fun updateStatus(newStatus: ServerStatus) {
        status = newStatus
        lastStatusChange = System.currentTimeMillis()
    }
}