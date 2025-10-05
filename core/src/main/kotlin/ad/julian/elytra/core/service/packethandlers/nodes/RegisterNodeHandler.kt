package ad.julian.elytra.core.service.packethandlers.nodes

import ad.julian.elytra.core.model.RunningNode
import ad.julian.elytra.core.service.NodeRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.RegisterNodePacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RegisterNodeHandler(val nodeRegistry: NodeRegistry) : PacketHandler<RegisterNodePacket> {
    val logger = LoggerFactory.getLogger(RegisterNodeHandler::class.java)
    override val type = PacketType.REGISTER_NODE_PACKET
    override fun handle(session: WebSocketService.WrappedSession, packet: RegisterNodePacket) {
        logger.info("Registering node with id ${packet.id}")
        nodeRegistry.registerNode(
            RunningNode(
                id = packet.id,
                session = session
            )
        )
    }
}