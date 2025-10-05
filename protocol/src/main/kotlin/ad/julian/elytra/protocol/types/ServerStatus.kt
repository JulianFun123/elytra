package ad.julian.elytra.protocol.types


enum class ServerStatus {
    NOT_STARTED,
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED,
    ERROR,
    UNRESPONSIVE
}