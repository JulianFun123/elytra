package ad.julian.elytra.core.controller

import ad.julian.elytra.core.model.RunningNode
import ad.julian.elytra.core.service.CloudPlayerRegistry
import ad.julian.elytra.core.service.NodeRegistry
import ad.julian.elytra.core.service.ProxyRegistry
import ad.julian.elytra.core.service.ServerRegistry
import ad.julian.elytra.protocol.types.CloudPlayer
import ad.julian.elytra.protocol.types.RunningProxy
import ad.julian.elytra.protocol.types.RunningServer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
class ServersController(
    val serverRegistry: ServerRegistry,
    val proxyRegistry: ProxyRegistry,
    val playerRegistry: CloudPlayerRegistry,
    val nodeRegistry: NodeRegistry
) {

    @GetMapping("/servers")
    fun getServers(): Collection<RunningServer> {
        return serverRegistry.runningServers.values
    }

    @GetMapping("/proxies")
    fun getProxies(): MutableCollection<RunningProxy> {
        return proxyRegistry.runningProxies.values
    }

    @GetMapping("/players")
    fun getPlayers(): MutableCollection<CloudPlayer> {
        return playerRegistry.players.values
    }

    @GetMapping("/nodes")
    fun getNodes(): MutableMap<String, RunningNode> {
        return nodeRegistry.nodes
    }

    @GetMapping("/stop")
    fun stop() {
        System.exit(0)
    }
}