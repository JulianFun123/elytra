package ad.julian.elytra.node.services

import ad.julian.elytra.node.helper.createInstanceConfig
import ad.julian.elytra.node.helper.movePluginToInstance
import ad.julian.elytra.protocol.client.proxy.UpdateProxyStatusPacket
import ad.julian.elytra.protocol.nodes.proxycontainer.CreateProxyContainerPacket
import ad.julian.elytra.protocol.nodes.proxycontainer.CreatedProxyContainerPacket
import ad.julian.elytra.protocol.types.RunningProxy
import ad.julian.elytra.protocol.types.ServerStatus
import jakarta.annotation.PreDestroy
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
class ProxyContainerRegistry(
    val dockerContainerRegistry: DockerContainerRegistry,
) {
    val runningProxies = mutableMapOf<String, RunningProxy>()

    val logger = LoggerFactory.getLogger(ProxyContainerRegistry::class.java)

    fun createProxy(request: CreateProxyContainerPacket) {
        val proxy = request.proxy
        val template = request.template

        val hostDir = File(template.dir)

        FileUtils.write(File("${hostDir.absolutePath}/forwarding.secret"), request.forwardingSecret, "UTF-8")

        val id = proxy.name

        createInstanceConfig(
            id = id,
            file = File(hostDir, "elytra.properties")
        )

        template.config["player-info-forwarding-mode"] = "modern"
        //template.config["[server]"] = null
        //template.config["enabled"] = "true"
        FileUtils.write(
            File("${hostDir.absolutePath}/velocity.toml"),
            template.config.map { it.key + (if (it.value != null) " = " + '"' + it.value + '"' else "") }
                .joinToString("\n"),
            "UTF-8"
        )

        logger.info("Copying cloud plugin .jar to plugins directory...")
        movePluginToInstance("velocity-all.jar", hostDir, this::class.java)
        logger.info("Plugins directory and velocity-all.jar are set up.")

        logger.info("[PROXY:${proxy.name}] Creating proxy with ID $id")

        val exec = dockerContainerRegistry.createContainerBuilder(
            name = id,
            image = template.image,
            hostname = id,
            env = mapOf(
                "TYPE" to template.type.name,
                "VERSION" to template.version,
                "MEMORY" to "${template.memory}M",
                "EULA" to "true",
                "SERVER_PORT" to proxy.portMapping.first().split(":")[1]
            ) + (template.environment ?: emptyMap()),
            portMapping = proxy.portMapping.associate {
                val (hostPort, containerPort) = it.split(":")
                hostPort to containerPort
            },
            volumes = mapOf(
                "${hostDir.absolutePath}" to "/server"
            )
        ).exec()

        logger.info("[PROXY:${proxy.name}->${id}] Created container with ID ${exec.id}")

        logger.info("[PROXY:${proxy.name}->${id}] Connecting container to network ${template.network}...")

        dockerContainerRegistry.dockerClient.connectToNetworkCmd()
            .withContainerId(exec.id)
            .withNetworkId(template.network)
            .exec()

        logger.info("[PROXY:${proxy.name}->${id}] Connected container to network ${template.network}...")

        val server = RunningProxy(
            id = id,
            proxy = proxy,
            status = ServerStatus.NOT_STARTED,
            containerId = exec.id,
        )

        runningProxies[server.id] = server

        logger.info("[PROXY:${proxy.name}->${id}] Added running server to registry")


        MasterConnectionService.conn?.send(
            CreatedProxyContainerPacket(
                packetId = request.packetId,
                server
            )
        )
    }

    fun startProxy(server: RunningProxy) {
        logger.info("[PROXY:${server.id}] Starting proxy...")

        try {
            MasterConnectionService.conn?.send(
                UpdateProxyStatusPacket(
                    id = server.id,
                    status = ServerStatus.STARTING
                )
            )
            dockerContainerRegistry.dockerClient.startContainerCmd(server.containerId).exec()
            logger.info("[PROXY:${server.id}] Proxy starting.")
        } catch (e: Exception) {

            MasterConnectionService.conn?.send(
                UpdateProxyStatusPacket(
                    id = server.id,
                    status = ServerStatus.ERROR
                )
            )
            logger.error("[PROXY:${server.id}] Error starting proxy: ${e.message}")
        }
    }

    fun stopProxy(id: String) {
        val server = runningProxies[id] ?: throw IllegalArgumentException("Proxy not found")
        logger.info("Proxy server ${server.id}...")


        MasterConnectionService.conn?.send(
            UpdateProxyStatusPacket(
                id = server.id,
                status = ServerStatus.STOPPING
            )
        )

        try {
            dockerContainerRegistry.dockerClient.stopContainerCmd(server.containerId).exec()
            dockerContainerRegistry.dockerClient.removeContainerCmd(server.containerId).exec()

            runningProxies.remove(server.id)
            logger.info("Server ${server.id} stopped.")
        } catch (e: Exception) {
            logger.error("Error stopping server ${server.id}: ${e.message}")
        }
    }

    @PreDestroy
    fun onStop() {
        logger.info("Shutting down, stopping all running servers...")
        runningProxies.values.toList().forEach { proxy ->
            logger.info("Stopping server ${proxy.id}...")
            stopProxy(proxy.id)
        }
        logger.info("All servers stopped.")
    }
}