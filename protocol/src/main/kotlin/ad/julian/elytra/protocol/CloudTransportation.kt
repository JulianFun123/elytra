package ad.julian.elytra.protocol

interface CloudTransportation {
    val generalPacketHandlers: MutableList<(Packet, PacketType) -> Unit>
    val packetHandlers: MutableMap<PacketType, MutableList<(Packet) -> Unit>>

    fun send(packet: Packet)


    fun onPacket(handler: (Packet, PacketType) -> Unit) {
        generalPacketHandlers.add(handler)
    }

    fun <T : Packet> onPacket(type: PacketType, handler: (T) -> Unit) {
        packetHandlers.computeIfAbsent(type) { mutableListOf() }.add({
            handler(it as T)
        })
    }

    fun <T : Packet> onPacket(clazz: Class<T>, handler: (T) -> Unit) {
        val type = PacketType.entries.find { it.type == clazz }
            ?: throw IllegalArgumentException("No PacketType found for class ${clazz.simpleName}")
        onPacket(type, handler)
    }
}