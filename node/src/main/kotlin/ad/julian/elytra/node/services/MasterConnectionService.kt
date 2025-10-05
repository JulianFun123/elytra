package ad.julian.elytra.node.services

import ad.julian.elytra.node.services.handlers.NodePacketHandler
import ad.julian.elytra.protocol.CloudClient
import ad.julian.elytra.protocol.CloudTransportation
import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.nodes.RegisterNodePacket
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MasterConnectionService(
    handlers: List<NodePacketHandler<out Packet>>
) {

    val logger = LoggerFactory.getLogger(MasterConnectionService::class.java)

    init {
        conn?.send(
            RegisterNodePacket(id)
        )

        handlers.forEach {
            logger.info("Registered handler for packet type ${it.type.name}")
        }
        conn?.onPacket { packet, type ->
            handlers.forEach {
                if (type == it.type) {
                    logger.info("Handling packet of type ${type.name}")
                    it.handlePacket(packet)
                }
            }
        }
    }

    val connection: CloudTransportation?
        get() = conn

    val id: String
        get() = Companion.id

    companion object {
        var conn: CloudTransportation? = CloudClient("ws://localhost:8080/ws")

        var id = "master"

        init {
            (conn as CloudClient).connect()
        }
    }
}