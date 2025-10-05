package ad.julian.elytra.core.service

import ad.julian.elytra.protocol.nodes.servercontainer.CreateServerContainerPacket
import ad.julian.elytra.protocol.nodes.servercontainer.CreatedServerContainerPacket
import ad.julian.elytra.protocol.nodes.servercontainer.StartServerContainerPacket
import ad.julian.elytra.protocol.nodes.servercontainer.StopServerContainerPacket
import ad.julian.elytra.protocol.types.RunningServer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ServerRegistry(
    val configs: ConfigManager,
    val proxyRegistry: ProxyRegistry,
    val webSocketService: WebSocketService,
    val nodeRegistry: NodeRegistry
) {
    val runningServers = mutableMapOf<String, RunningServer>()

    val logger = LoggerFactory.getLogger(ServerRegistry::class.java)

    fun createServer(groupName: String): RunningServer {
        val group = configs.groupsConfig.groups.first { it.name == groupName }
        val template = configs.templatesConfig.templates.first { it.name == group.templateName }

        val createServerContainerPacket = CreateServerContainerPacket(
            group = group,
            template = template,
            nodeId = "master",
            forwardingSecret = proxyRegistry.forwardingSecret
        )

        val get = webSocketService.sendWithResponse<CreatedServerContainerPacket>(
            nodeRegistry.getNode("master")!!.session, createServerContainerPacket, 10000
        ).get()

        runningServers[get.server.id] = get.server

        return get.server
    }

    fun startServer(id: String) {
        webSocketService.broadcast(StartServerContainerPacket(id))
    }

    fun stopServer(id: String) {
        webSocketService.broadcast(StopServerContainerPacket(id))
    }

    fun getRunningServersList(): Collection<RunningServer> = runningServers.values

    fun getServersByGroup(groupName: String): List<RunningServer> =
        runningServers.values.filter { it.group.name == groupName }
}