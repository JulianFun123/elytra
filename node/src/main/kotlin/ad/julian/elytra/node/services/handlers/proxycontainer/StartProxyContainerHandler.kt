package ad.julian.elytra.node.services.handlers.proxycontainer

import ad.julian.elytra.node.services.ProxyContainerRegistry
import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.proxycontainer.StartProxyContainerPacket
import org.springframework.stereotype.Service

@Service
class StartProxyContainerHandler(
    val proxyRegistry: ProxyContainerRegistry
) :
    NodePacketHandler<StartProxyContainerPacket> {
    override val type = PacketType.START_PROXY_CONTAINER_PACKET
    override fun handle(packet: StartProxyContainerPacket) {
        if (proxyRegistry.runningProxies.containsKey(packet.proxyId)) {
            proxyRegistry.startProxy(proxyRegistry.runningProxies.get(packet.proxyId)!!)
        }
    }
}