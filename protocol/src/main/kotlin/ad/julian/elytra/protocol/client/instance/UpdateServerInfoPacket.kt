package ad.julian.elytra.protocol.client.instance

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.ServerInfo

data class UpdateServerInfoPacket(
    val id: String,
    val serverInfo: ServerInfo,
) : Packet(PacketType.UPDATE_SERVER_INFO_PACKET)
