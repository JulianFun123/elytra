package ad.julian.elytra.core.service.packethandlers.proxy

import ad.julian.elytra.core.service.ProxyRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.proxy.ServerRemovedFromProxyPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ServerRemovedFromProxyHandler(val proxyRegistry: ProxyRegistry) : PacketHandler<ServerRemovedFromProxyPacket> {
    val logger = LoggerFactory.getLogger(ServerRemovedFromProxyHandler::class.java)
    override val type = PacketType.SERVER_REMOVED_FROM_PROXY
    override fun handle(session: WebSocketService.WrappedSession, packet: ServerRemovedFromProxyPacket) {
        proxyRegistry.runningProxies[packet.proxyId]?.let {
            it.registeredServers.remove(packet.id)
            logger.info("Server ${packet.id} removed from ${it.proxy.name})")
        }
    }
}