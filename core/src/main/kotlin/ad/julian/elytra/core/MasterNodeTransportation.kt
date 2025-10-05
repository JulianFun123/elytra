package ad.julian.elytra.core

import ad.julian.elytra.core.service.WebSocketService.WrappedSession
import ad.julian.elytra.core.websocket.WebSocketHandler
import ad.julian.elytra.protocol.CloudTransportation
import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import org.slf4j.LoggerFactory

class MasterNodeTransportation(val webSocketHandler: WebSocketHandler) : CloudTransportation {
    override val generalPacketHandlers: MutableList<(Packet, PacketType) -> Unit> =
        mutableListOf()
    override val packetHandlers: MutableMap<PacketType, MutableList<(Packet) -> Unit>> =
        mutableMapOf()

    val logger = LoggerFactory.getLogger(MasterNodeTransportation::class.java)

    val wrappedSession = object : WrappedSession {
        override val id = "master-connection"
        override fun send(packet: Packet) {
            generalPacketHandlers.forEach {
                it.invoke(packet, packet.type)
            }
            packetHandlers[packet.type]?.forEach { handler -> handler(packet) }
        }

        override fun close() {
            // Cannot close master connection
        }

        override val isOpen: Boolean
            get() = true
    }


    override fun send(packet: Packet) {
        // logger.info("Received inxane cloud protocol: ${InxaneCloudProtocol.mapper.writeValueAsString(packet)}")
        webSocketHandler.handlePacket(wrappedSession, packet)
    }
}