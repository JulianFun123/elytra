package ad.julian.elytra.protocol.client.proxy

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class RemovePlayerPacket(
    val uuid: String
) : Packet(PacketType.REMOVE_PLAYER_PACKET)