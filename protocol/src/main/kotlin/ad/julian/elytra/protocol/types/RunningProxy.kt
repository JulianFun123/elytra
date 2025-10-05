package ad.julian.elytra.protocol.types

data class RunningProxy(
    var id: String,
    var proxy: Proxy,
    var status: ServerStatus,
    var containerId: String,
    var creationTime: Long = System.currentTimeMillis(),
    var lastStatusChange: Long = System.currentTimeMillis(),
    var registeredServers: MutableSet<String> = mutableSetOf()
) {
    fun updateStatus(newStatus: ServerStatus) {
        status = newStatus
        lastStatusChange = System.currentTimeMillis()
    }
}