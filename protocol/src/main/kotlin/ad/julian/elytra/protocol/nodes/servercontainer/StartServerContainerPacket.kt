package ad.julian.elytra.protocol.nodes.servercontainer

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class StartServerContainerPacket(
    val serverId: String
) : Packet(PacketType.START_SERVER_CONTAINER_PACKET)