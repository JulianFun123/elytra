package ad.julian.elytra.core.service.packethandlers

import ad.julian.elytra.core.service.WebSocketService
import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

interface PacketHandler<T : Packet> {
    val type: PacketType
    fun handle(session: WebSocketService.WrappedSession, packet: T)
}