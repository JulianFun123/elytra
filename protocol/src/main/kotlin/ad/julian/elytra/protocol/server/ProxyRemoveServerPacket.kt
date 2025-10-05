package ad.julian.elytra.protocol.server

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class ProxyRemoveServerPacket(
    //val proxyId: String,
    val serverId: String,
) : Packet(PacketType.PROXY_REMOVE_SERVER)
