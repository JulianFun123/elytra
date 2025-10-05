package ad.julian.elytra.protocol.nodes.servercontainer

import ad.julian.elytra.protocol.PacketResponse
import ad.julian.elytra.protocol.PacketType
import ad.julian.elytra.protocol.types.RunningServer

data class CreatedServerContainerPacket(
    override val packetId: String,
    val server: RunningServer,
) : PacketResponse(packetId, type = PacketType.CREATED_SERVER_CONTAINER_PACKET)