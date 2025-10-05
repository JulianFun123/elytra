package ad.julian.elytra.protocol.nodes.proxycontainer

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class StopProxyContainerPacket(
    val proxyId: String
) : Packet(PacketType.STOP_PROXY_CONTAINER_PACKET)