package ad.julian.elytra.node.services.handlers

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

interface NodePacketHandler<T : Packet> {
    val type: PacketType
    fun handle(packet: T)
    fun handlePacket(packet: Packet) {
        @Suppress("UNCHECKED_CAST")
        handle(packet as T)
    }
}