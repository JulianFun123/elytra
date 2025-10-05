package ad.julian.elytra.core.service.packethandlers.server

import ad.julian.elytra.core.service.ServerRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.instance.UpdateServerInfoPacket
import ad.julian.elytra.protocol.helper.mergeWith
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UpdateServerInfoHandler(val serverRegistry: ServerRegistry) : PacketHandler<UpdateServerInfoPacket> {
    val logger = LoggerFactory.getLogger(UpdateServerInfoHandler::class.java)
    override val type = PacketType.UPDATE_SERVER_INFO_PACKET
    override fun handle(session: WebSocketService.WrappedSession, packet: UpdateServerInfoPacket) {
        val server = serverRegistry.runningServers[packet.id]
        /*if (server != null && server.status != packet.serverStatus) {
            logger.info("Updated server ${packet.id} status to ${packet.serverStatus}")
        } */

        server?.info = server.info.mergeWith(packet.serverInfo)
    }
}