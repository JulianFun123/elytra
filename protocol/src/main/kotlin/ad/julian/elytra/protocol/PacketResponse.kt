package ad.julian.elytra.protocol

open class PacketResponse(
    open val packetId: String,
    type: PacketType
) : Packet(type)