package ad.julian.elytra.protocol.client.proxy

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.CloudPlayer

data class PutPlayerPacket(
    val player: CloudPlayer
) : Packet(PacketType.PUT_PLAYER_PACKET)
