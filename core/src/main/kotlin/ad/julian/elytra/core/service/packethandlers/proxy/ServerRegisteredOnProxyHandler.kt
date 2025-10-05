package ad.julian.elytra.core.service.packethandlers.proxy

import ad.julian.elytra.core.service.ProxyRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.proxy.ServerRegisteredOnProxyPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ServerRegisteredOnProxyHandler(val proxyRegistry: ProxyRegistry) : PacketHandler<ServerRegisteredOnProxyPacket> {
    val logger = LoggerFactory.getLogger(ServerRegisteredOnProxyHandler::class.java)
    override val type = PacketType.SERVER_REGISTERED_ON_PROXY
    override fun handle(session: WebSocketService.WrappedSession, packet: ServerRegisteredOnProxyPacket) {
        proxyRegistry.runningProxies[packet.proxyId]?.let {
            it.registeredServers.add(packet.id)
            logger.info("Server ${packet.id} registered on ${it.proxy.name})")
        }
    }
}