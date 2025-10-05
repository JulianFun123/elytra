package ad.julian.elytra.protocol

import java.util.*

open class PacketWithResponse(
    val packetId: String = UUID.randomUUID().toString(),
    var responseType: PacketType,
    type: PacketType
) : Packet(type)