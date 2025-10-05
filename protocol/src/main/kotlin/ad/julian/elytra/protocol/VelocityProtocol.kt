package ad.julian.elytra.protocol

import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object VelocityProtocol {
    val version = 1
    val mapper = jacksonObjectMapper()

    init {
        PacketType.entries.forEach { packetType ->
            mapper.registerSubtypes(
                NamedType(packetType.type, packetType.name)
            )
        }
    }

    fun fromJson(json: String): Packet {
        val node = mapper.readTree(json)
        val valueOf = PacketType.valueOf(node.get("type").asText())
        return mapper.readValue(json, valueOf.type)
    }
}