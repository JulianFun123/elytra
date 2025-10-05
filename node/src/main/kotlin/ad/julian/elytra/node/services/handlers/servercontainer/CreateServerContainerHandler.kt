package ad.julian.elytra.node.services.handlers.servercontainer

import ad.julian.elytra.node.services.MasterConnectionService
import ad.julian.elytra.node.services.ServerContainerRegistry
import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.servercontainer.CreateServerContainerPacket
import org.springframework.stereotype.Service

@Service
class CreateServerContainerHandler(
    val serverContainerRegistry: ServerContainerRegistry
) :
    NodePacketHandler<CreateServerContainerPacket> {
    override val type = PacketType.CREATE_SERVER_CONTAINER_PACKET
    override fun handle(packet: CreateServerContainerPacket) {
        if (packet.nodeId != MasterConnectionService.id) return
        serverContainerRegistry.createServer(packet.packetId, packet.group, packet.template, packet.forwardingSecret)
    }
}