package ad.julian.elytra.protocol.client.proxy

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class ServerRemovedFromProxyPacket(
    val proxyId: String,
    val id: String
) : Packet(PacketType.SERVER_REMOVED_FROM_PROXY)
