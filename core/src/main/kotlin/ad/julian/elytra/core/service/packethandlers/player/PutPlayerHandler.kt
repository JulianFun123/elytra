package ad.julian.elytra.core.service.packethandlers.player

import ad.julian.elytra.core.service.CloudPlayerRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.proxy.PutPlayerPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PutPlayerHandler(val cloudPlayerRegistry: CloudPlayerRegistry) : PacketHandler<PutPlayerPacket> {
    val logger = LoggerFactory.getLogger(PutPlayerPacket::class.java)
    override val type = PacketType.PUT_PLAYER_PACKET
    override fun handle(session: WebSocketService.WrappedSession, packet: PutPlayerPacket) {
        cloudPlayerRegistry.putPlayer(packet.player)
        logger.info("Added/Updated player ${packet.player.userName} (${packet.player.uuid}) in registry")
    }
}