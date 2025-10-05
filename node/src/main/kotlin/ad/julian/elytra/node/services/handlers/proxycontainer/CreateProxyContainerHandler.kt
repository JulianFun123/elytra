package ad.julian.elytra.node.services.handlers.proxycontainer

import ad.julian.elytra.node.services.MasterConnectionService
import ad.julian.elytra.node.services.ProxyContainerRegistry
import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.proxycontainer.CreateProxyContainerPacket
import org.springframework.stereotype.Service

@Service
class CreateProxyContainerHandler(
    val proxyRegistry: ProxyContainerRegistry
) :
    NodePacketHandler<CreateProxyContainerPacket> {
    override val type = PacketType.CREATE_PROXY_CONTAINER_PACKET
    override fun handle(packet: CreateProxyContainerPacket) {
        if (packet.nodeId != MasterConnectionService.id) return
        proxyRegistry.createProxy(packet)
    }
}