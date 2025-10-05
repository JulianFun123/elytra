package ad.julian.elytra.protocol.client.proxy

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.ServerStatus

data class UpdateProxyStatusPacket(
    val id: String,
    val status: ServerStatus
) : Packet(PacketType.UPDATE_PROXY_STATUS)
