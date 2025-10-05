package ad.julian.elytra.core.service.packethandlers.server

import ad.julian.elytra.core.service.ServerRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.instance.UpdateServerStatusPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UpdateServerStatusHandler(val serverRegistry: ServerRegistry) : PacketHandler<UpdateServerStatusPacket> {
    val logger = LoggerFactory.getLogger(UpdateServerStatusHandler::class.java)
    override val type = PacketType.UPDATE_SERVER_STATUS_PACKET
    override fun handle(session: WebSocketService.WrappedSession, packet: UpdateServerStatusPacket) {
        val server = serverRegistry.runningServers[packet.id]
        if (server != null && server.status != packet.serverStatus) {
            logger.info("Updated server ${packet.id} status to ${packet.serverStatus}")
        }
        server?.updateStatus(packet.serverStatus)
    }
}