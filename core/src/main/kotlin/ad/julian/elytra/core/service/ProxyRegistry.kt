package ad.julian.elytra.core.service

import ad.julian.elytra.protocol.nodes.proxycontainer.CreateProxyContainerPacket
import ad.julian.elytra.protocol.nodes.proxycontainer.CreatedProxyContainerPacket
import ad.julian.elytra.protocol.nodes.proxycontainer.StartProxyContainerPacket
import ad.julian.elytra.protocol.nodes.proxycontainer.StopProxyContainerPacket
import ad.julian.elytra.protocol.types.RunningProxy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProxyRegistry(
    val configs: ConfigManager,
    val webSocketService: WebSocketService,
    val nodeRegistry: NodeRegistry
) {
    val runningProxies = mutableMapOf<String, RunningProxy>()

    val forwardingSecret = UUID.randomUUID().toString().replace("-", "").substring(0, 16)

    val logger = LoggerFactory.getLogger(ProxyRegistry::class.java)

    fun createProxy(proxyName: String): RunningProxy {
        val proxy = configs.proxiesConfig.proxies.first { it.name == proxyName }
        val template = configs.proxiesConfig.templates.first { it.name == proxy.templateName }
        val createProxyContainerPacket = CreateProxyContainerPacket(
            proxy = proxy,
            template = template,
            forwardingSecret = forwardingSecret,
            nodeId = "master",
        )


        val get = webSocketService.sendWithResponse<CreatedProxyContainerPacket>(
            nodeRegistry.getNode("master")!!.session, createProxyContainerPacket, 10000
        ).get()

        runningProxies[get.proxy.id] = get.proxy

        return get.proxy
    }

    fun startProxy(server: RunningProxy) {
        webSocketService.broadcast(StartProxyContainerPacket(server.id))
    }

    fun stopProxy(id: String) {
        webSocketService.broadcast(StopProxyContainerPacket(id))
    }
}