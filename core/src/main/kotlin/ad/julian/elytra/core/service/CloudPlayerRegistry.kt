package ad.julian.elytra.core.service

import ad.julian.elytra.protocol.types.CloudPlayer
import org.springframework.stereotype.Service

@Service
class CloudPlayerRegistry {
    val players = mutableMapOf<String, CloudPlayer>()

    fun getPlayer(uuid: String): CloudPlayer? {
        return players[uuid]
    }

    fun putPlayer(player: CloudPlayer) {
        players[player.uuid] = player
    }

    fun removePlayer(uuid: String) {
        players.remove(uuid)
    }
}