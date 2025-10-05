package ad.julian.elytra.node.services.handlers.proxycontainer

import ad.julian.elytra.node.services.ProxyContainerRegistry
import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.nodes.proxycontainer.StopProxyContainerPacket
import org.springframework.stereotype.Service

@Service
class StopProxyContainerHandler(
    val proxyRegistry: ProxyContainerRegistry
) :
    NodePacketHandler<StopProxyContainerPacket> {
    override val type = PacketType.STOP_PROXY_CONTAINER_PACKET
    override fun handle(packet: StopProxyContainerPacket) {
        if (proxyRegistry.runningProxies.containsKey(packet.proxyId)) {
            proxyRegistry.stopProxy(packet.proxyId)
        }
    }
}