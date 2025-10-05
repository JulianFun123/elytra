package ad.julian.elytra.protocol.nodes.proxycontainer

import ad.julian.elytra.protocol.PacketResponse
import ad.julian.elytra.protocol.PacketType

data class RemovedProxyContainerPacket(
    override val packetId: String,
    val proxyId: String,
) : PacketResponse(packetId, type = PacketType.REMOVED_PROXY_CONTAINER_PACKET)