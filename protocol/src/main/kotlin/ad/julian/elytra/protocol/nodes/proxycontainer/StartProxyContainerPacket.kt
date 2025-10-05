package ad.julian.elytra.protocol.nodes.proxycontainer

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class StartProxyContainerPacket(
    val proxyId: String
) : Packet(PacketType.START_PROXY_CONTAINER_PACKET)