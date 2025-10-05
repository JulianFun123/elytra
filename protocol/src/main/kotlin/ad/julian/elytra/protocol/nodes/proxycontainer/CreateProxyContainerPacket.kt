package ad.julian.elytra.protocol.nodes.proxycontainer

import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.PacketWithResponse
import ad.julian.elytra.protocol.types.Proxy
import ad.julian.elytra.protocol.types.ProxyTemplate

data class CreateProxyContainerPacket(
    val nodeId: String,
    val proxy: Proxy,
    val template: ProxyTemplate,
    val forwardingSecret: String
) : PacketWithResponse(
    type = PacketType.CREATE_PROXY_CONTAINER_PACKET,
    responseType = PacketType.CREATED_PROXY_CONTAINER_PACKET
)