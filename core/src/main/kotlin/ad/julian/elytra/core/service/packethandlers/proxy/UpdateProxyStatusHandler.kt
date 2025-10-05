package ad.julian.elytra.core.service.packethandlers.proxy

import ad.julian.elytra.core.service.ProxyRegistry
import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.client.proxy.UpdateProxyStatusPacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UpdateProxyStatusHandler(val proxyRegistry: ProxyRegistry) : PacketHandler<UpdateProxyStatusPacket> {
    val logger = LoggerFactory.getLogger(UpdateProxyStatusHandler::class.java)
    override val type = PacketType.UPDATE_PROXY_STATUS
    override fun handle(session: WebSocketService.WrappedSession, packet: UpdateProxyStatusPacket) {
        val proxy = proxyRegistry.runningProxies[packet.id]
        if (proxy?.status != packet.status) {
            logger.info("Updated proxy ${packet.id} status to ${packet.status}")
        }
        proxy?.status = packet.status
    }
}