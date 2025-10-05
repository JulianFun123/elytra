package ad.julian.elytra.protocol.nodes

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class RegisterNodePacket(
    val id: String
) : Packet(PacketType.REGISTER_NODE_PACKET)