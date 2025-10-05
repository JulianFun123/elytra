package ad.julian.elytra.node.services.handlers.servercontainer

import ad.julian.elytra.node.services.ServerContainerRegistry
import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.servercontainer.StartServerContainerPacket
import org.springframework.stereotype.Service

@Service
class StartServerContainerHandler(
    val serverContainerRegistry: ServerContainerRegistry
) :
    NodePacketHandler<StartServerContainerPacket> {
    override val type = PacketType.START_SERVER_CONTAINER_PACKET
    override fun handle(packet: StartServerContainerPacket) {
        if (serverContainerRegistry.runningServers.containsKey(packet.serverId)) {
            serverContainerRegistry.startServer(serverContainerRegistry.runningServers.get(packet.serverId)!!)
        }
    }
}