package ad.julian.elytra.protocol.nodes.servercontainer

import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.PacketWithResponse
import ad.julian.elytra.protocol.types.ServerGroup
import ad.julian.elytra.protocol.types.ServerTemplate

data class CreateServerContainerPacket(
    val nodeId: String,
    val group: ServerGroup,
    val template: ServerTemplate,
    val forwardingSecret: String
) : PacketWithResponse(
    type = PacketType.CREATE_SERVER_CONTAINER_PACKET,
    responseType = PacketType.CREATED_SERVER_CONTAINER_PACKET
)