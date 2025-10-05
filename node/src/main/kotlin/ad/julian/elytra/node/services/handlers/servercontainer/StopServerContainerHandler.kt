package ad.julian.elytra.node.services.handlers.servercontainer

import ad.julian.elytra.node.services.ServerContainerRegistry
import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.servercontainer.StopServerContainerPacket
import org.springframework.stereotype.Service

@Service
class StopServerContainerHandler(
    val serverContainerRegistry: ServerContainerRegistry
) :
    NodePacketHandler<StopServerContainerPacket> {
    override val type = PacketType.STOP_SERVER_CONTAINER_PACKET
    override fun handle(packet: StopServerContainerPacket) {
        if (serverContainerRegistry.runningServers.containsKey(packet.serverId)) {
            serverContainerRegistry.stopServer(packet.serverId)
        }
    }
}