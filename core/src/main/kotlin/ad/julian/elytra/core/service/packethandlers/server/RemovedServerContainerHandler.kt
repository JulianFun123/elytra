package ad.julian.elytra.core.service.packethandlers.server

import ad.julian.elytra.core.service.ServerRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.servercontainer.RemovedServerContainerPacket
import ad.julian.elytra.protocol.server.ProxyRemoveServerPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RemovedServerContainerHandler(val webSocketService: WebSocketService, val serverRegistry: ServerRegistry) :
    PacketHandler<RemovedServerContainerPacket> {
    val logger = LoggerFactory.getLogger(RemovedServerContainerHandler::class.java)
    override val type = PacketType.REMOVED_SERVER_CONTAINER_PACKET
    override fun handle(session: WebSocketService.WrappedSession, packet: RemovedServerContainerPacket) {
        val server = serverRegistry.runningServers[packet.serverId]

        webSocketService.broadcast(
            ProxyRemoveServerPacket(
                serverId = packet.serverId,
            )
        )
        val container = serverRegistry.runningServers.remove(packet.serverId)
        if (container != null) {
            logger.info("Removed server container ${packet.serverId} (${container})")
        }
    }
}