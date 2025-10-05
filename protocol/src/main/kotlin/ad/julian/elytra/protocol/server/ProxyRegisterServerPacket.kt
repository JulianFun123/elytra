package ad.julian.elytra.protocol.server

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.ServerGroup

data class ProxyRegisterServerPacket(
    val proxyId: String,
    val serverId: String,
    val address: String,
    val port: Int,
    val group: ServerGroup
) : Packet(PacketType.PROXY_REGISTER_SERVER)
