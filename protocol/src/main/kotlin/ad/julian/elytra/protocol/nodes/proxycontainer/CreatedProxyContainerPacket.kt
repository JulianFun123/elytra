package ad.julian.elytra.protocol.nodes.proxycontainer

import ad.julian.elytra.protocol.PacketResponse
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.RunningProxy

data class CreatedProxyContainerPacket(
    override val packetId: String,
    val proxy: RunningProxy,
) : PacketResponse(packetId, type = PacketType.CREATED_PROXY_CONTAINER_PACKET)