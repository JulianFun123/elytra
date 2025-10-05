package ad.julian.elytra.core.websocket

import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.core.service.packethandlers.PacketHandler
import ad.julian.elytra.protocol.VelocityProtocol
import ad.julian.elytra.protocol.Packet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSocketHandler(private val webSocketService: WebSocketService, handlers: List<PacketHandler<out Packet>>) :
    TextWebSocketHandler() {

    val logger = LoggerFactory.getLogger(WebSocketHandler::class.java)
    private val handlerMap = handlers.associateBy { it.type }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        webSocketService.register(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        //logger.info("Received inxane cloud protocol: ${message.payload}")
        val packet = VelocityProtocol.fromJson(message.payload)

        handlePacket(webSocketService.getByWebSocketSession(session)!!, packet)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        webSocketService.unregister(session)
    }

    fun handlePacket(session: WebSocketService.WrappedSession, packet: Packet) {
        handlerMap[packet.type]?.let { (it as PacketHandler<Packet>).handle(session, packet) }
        webSocketService.handlers.forEach { handler ->
            if (handler.key == packet.type) {
                handler.value(packet)
            }
        }
    }
}