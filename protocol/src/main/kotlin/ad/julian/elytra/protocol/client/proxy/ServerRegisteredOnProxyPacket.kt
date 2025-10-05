package ad.julian.elytra.protocol.client.proxy

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class ServerRegisteredOnProxyPacket(
    val proxyId: String,
    val id: String
) : Packet(PacketType.SERVER_REGISTERED_ON_PROXY)
