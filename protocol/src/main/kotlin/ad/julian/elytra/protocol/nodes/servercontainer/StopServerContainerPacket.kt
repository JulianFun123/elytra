package ad.julian.elytra.protocol.nodes.servercontainer

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType

data class StopServerContainerPacket(
    val serverId: String
) : Packet(PacketType.STOP_SERVER_CONTAINER_PACKET)