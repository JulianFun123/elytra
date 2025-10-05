package ad.julian.elytra.core.service.packethandlers.player

import ad.julian.elytra.core.service.CloudPlayerRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.proxy.RemovePlayerPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RemovePlayerHandler(val cloudPlayerRegistry: CloudPlayerRegistry) : PacketHandler<RemovePlayerPacket> {
    val logger = LoggerFactory.getLogger(RemovePlayerPacket::class.java)
    override val type = PacketType.REMOVE_PLAYER_PACKET
    override fun handle(session: WebSocketService.WrappedSession, packet: RemovePlayerPacket) {
        cloudPlayerRegistry.removePlayer(packet.uuid)
        logger.info("Removed player with UUID ${packet.uuid} from registry")
    }
}