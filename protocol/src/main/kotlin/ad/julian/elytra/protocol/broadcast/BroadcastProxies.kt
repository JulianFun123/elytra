package ad.julian.elytra.protocol.broadcast

import ad.julian.elytra.protocol.Packet
import ad.julian.elytra.protocol.PacketType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Sends a packet through the master to all proxies.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BroadcastProxies<T : Packet>(
    val packet: T,
    val proxies: List<String>? = null,
) : Packet(PacketType.BROADCAST_PROXIES)
