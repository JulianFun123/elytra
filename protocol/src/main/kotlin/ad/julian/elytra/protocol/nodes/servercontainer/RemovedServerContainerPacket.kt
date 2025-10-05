package ad.julian.elytra.protocol.nodes.servercontainer

import ad.julian.elytra.protocol.PacketResponse
import ad.julian.elytra.protocol.PacketType

data class RemovedServerContainerPacket(
    override val packetId: String,
    val serverId: String,
) : PacketResponse(packetId, type = PacketType.REMOVED_SERVER_CONTAINER_PACKET)