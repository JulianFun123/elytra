package ad.julian.elytra.core.service

import ad.julian.elytra.protocol.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Service
class WebSocketService() {
    val handlers: MutableMap<PacketType, (Packet) -> Unit> = mutableMapOf()

    val logger = LoggerFactory.getLogger(WebSocketService::class.java)

    val sessions = mutableListOf<WrappedSession>()

    interface WrappedSession {
        fun send(packet: Packet)
        fun close()
        val isOpen: Boolean
        val id: String
    }

    class WrappedWebSocketSession(val session: WebSocketSession) : WrappedSession {
        override val id = session.id
        override fun send(packet: Packet) {
            if (session.isOpen) {
                session.sendMessage(TextMessage(VelocityProtocol.mapper.writeValueAsString(packet)))
            }
        }

        override fun close() {
            if (session.isOpen) {
                session.close()
            }
        }

        override val isOpen: Boolean
            get() = session.isOpen
    }

    fun register(session: WebSocketSession) {
        sessions.add(WrappedWebSocketSession(session))
    }

    fun unregister(session: WebSocketSession) {
        sessions.removeIf { it.id == session.id }
    }

    fun broadcast(packet: Packet) {
        sessions.forEach {
            //logger.info("Broadcasting ${packet::class.simpleName} to session ${it.id}")
            if (it.isOpen) {
                it.send(packet)
            }
        }
    }

    fun <T : PacketResponse> sendWithResponse(
        session: WrappedSession,
        packet: PacketWithResponse,
        timeoutMillis: Long = 5000
    ): CompletableFuture<T> {
        val responseType = packet.responseType
        val future = CompletableFuture<T>()

        val handler: (Packet) -> Unit = {
            if (it::class.java.simpleName == responseType.type.simpleName && (it is PacketResponse) && it.packetId == packet.packetId) {
                handlers.remove(responseType)
                @Suppress("UNCHECKED_CAST")
                future.complete(it as T)
            }
        }

        addHandler(responseType, handler)
        session.send(packet)

        Executors.newSingleThreadScheduledExecutor().schedule({
            if (!future.isDone) {
                handlers.remove(responseType)
                future.completeExceptionally(RuntimeException("Timeout receiving new server"))
            }
        }, timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS)

        return future
    }

    fun getByWebSocketSession(session: WebSocketSession): WrappedSession? {
        return sessions.find { it.id == session.id }
    }

    fun addHandler(type: PacketType, handler: (Packet) -> Unit) {
        handlers[type] = handler
    }

    fun <T : Packet> addHandler(clazz: Class<T>, handler: (T) -> Unit) {
        val type = PacketType.entries.find { it.type == clazz }
            ?: throw IllegalArgumentException("No PacketType found for class ${clazz.simpleName}")
        addHandler(type) { handler(it as T) }
    }
}