package ad.julian.elytra.protocol.client.instance

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.ServerStatus

data class UpdateServerStatusPacket(
    val id: String,
    val serverStatus: ServerStatus,
) : Packet(PacketType.UPDATE_SERVER_STATUS_PACKET)
