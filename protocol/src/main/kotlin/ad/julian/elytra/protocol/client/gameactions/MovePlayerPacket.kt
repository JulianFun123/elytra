package ad.julian.elytra.protocol.client.gameactions

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

class MovePlayerPacket(
    val playerUUID: String,
    val targetServer: String,
    // TODO: val targetProxy: String? = null,
) : Packet(PacketType.MOVE_PLAYER_PACKET)