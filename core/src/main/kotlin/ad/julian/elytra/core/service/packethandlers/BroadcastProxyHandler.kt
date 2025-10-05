package ad.julian.elytra.core.service.packethandlers

import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.broadcast.BroadcastProxies
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BroadcastProxyHandler(val webSocketService: WebSocketService) : PacketHandler<BroadcastProxies<*>> {
    val logger: Logger? = LoggerFactory.getLogger(BroadcastProxyHandler::class.java)
    override val type = PacketType.BROADCAST_PROXIES
    override fun handle(session: WebSocketService.WrappedSession, packet: BroadcastProxies<*>) {
        webSocketService.broadcast(packet.packet)
    }
}