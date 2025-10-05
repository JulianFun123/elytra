package ad.julian.elytra.node.services

import ad.julian.elytra.node.helper.createInstanceConfig
import ad.julian.elytra.protocol.client.instance.UpdateServerStatusPacket
import ad.julian.elytra.protocol.nodes.servercontainer.CreatedServerContainerPacket
import ad.julian.elytra.protocol.nodes.servercontainer.RemovedServerContainerPacket
import ad.julian.elytra.protocol.types.*
import com.github.jknack.handlebars.Handlebars
import jakarta.annotation.PreDestroy
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
class ServerContainerRegistry(
    val dockerContainerRegistry: DockerContainerRegistry,
) {
    val runningServers = mutableMapOf<String, RunningServer>()

    val logger = LoggerFactory.getLogger(ServerContainerRegistry::class.java)

    fun createServer(packetId: String, group: ServerGroup, template: ServerTemplate, forwardingSecret: String) {
        var id = group.name

        if (group.type == ServerGroupType.DYNAMIC) {
            var i = 1
            while (runningServers.containsKey("$id-$i")) {
                i++
            }
            id += "-$i"
        }

        var hostDir = File(template.dir)

        if (group.type == ServerGroupType.DYNAMIC) {
            hostDir = File("tmp/${id}")
            hostDir.mkdirs()

            FileUtils.copyDirectory(File(template.dir), hostDir)
        } else if (group.type == ServerGroupType.STATIC && group.dir != null && !group.dir!!.isEmpty()) {
            // Logic if static and dir is set in group, use that one.
            // Copy files if does not exist
            hostDir = File(group.dir!!)

            if (!hostDir.exists()) {
                FileUtils.copyDirectory(File(template.dir), hostDir)
            }
        }

        createInstanceConfig(
            id = id,
            file = File(hostDir, "elytra.properties")
        )

        logger.info("[${group.name}->$id] Creating server")

        FileUtils.write(
            File("${hostDir.absolutePath}/config/paper-global.yml"),
            """proxies:
  velocity:
    enabled: true
    online-mode: true
    secret: '${forwardingSecret}'""", "UTF-8"
        )

        FileUtils.write(
            File("${hostDir.absolutePath}/server.properties"),
            """server-port=25565
online-mode=false""",
            "UTF-8"
        )

        val memory = group.memory ?: template.memory ?: null

        val envs = (mapOf(
            "TYPE" to template.type,
            "VERSION" to template.version,
            if (memory != null) "MEMORY" to "${memory}M" else "PLACEHOLDER1" to "TRUE",
            "SERVER_PORT" to template.port.toString(),
            "EULA" to "true"
        ) + (template.environment ?: emptyMap()) + (group.environment ?: emptyMap())).map {
            val handlebars = Handlebars().compileInline(it.value)
            it.key to handlebars.apply(group)
        }.toMap()

        val exec = dockerContainerRegistry.createContainerBuilder(
            name = id,
            image = template.image,
            hostname = id,
            env = envs,
            volumes = mapOf(
                "${hostDir.absolutePath}" to "/data"
            )
        ).exec()

        logger.info("[${group.name}->${id}] Created container with ID ${exec.id}")

        logger.info("[${group.name}->${id}] Connecting container to network ${template.network}...")

        dockerContainerRegistry.dockerClient.connectToNetworkCmd()
            .withContainerId(exec.id)
            .withNetworkId(group.network ?: template.network ?: "bridge")
            .exec()

        logger.info("[${group.name}->${id}] Connected container to network ${template.network}...")

        val server = RunningServer(
            id = id,
            group = group,
            status = ServerStatus.NOT_STARTED,
            containerId = exec.id,
            nodeId = MasterConnectionService.id,
            labels = group.labels?.toMutableMap() ?: mutableMapOf()
        )

        runningServers[server.id] = server

        MasterConnectionService.conn?.send(
            CreatedServerContainerPacket(
                packetId = packetId,
                server
            )
        )

        logger.info("[${group.name}->${id}] Added running server to registry")
    }

    fun startServer(server: RunningServer) {
        logger.info("[${server.group.name}->${server.id}] Starting server...")

        try {
            MasterConnectionService.conn?.send(
                UpdateServerStatusPacket(
                    id = server.id,
                    serverStatus = ServerStatus.STARTING
                )
            )
            dockerContainerRegistry.dockerClient.startContainerCmd(server.containerId).exec()
            logger.info("[${server.group.name}->${server.id}] Server starting.")
        } catch (e: Exception) {
            MasterConnectionService.conn?.send(
                UpdateServerStatusPacket(
                    id = server.id,
                    serverStatus = ServerStatus.ERROR
                )
            )
            logger.error("[${server.group.name}->${server.id}] Error starting server: ${e.message}")
        }
    }

    fun stopServer(id: String) {
        val server = runningServers[id] ?: throw IllegalArgumentException("Server not found")
        logger.info("Stopping server ${server.id}...")
        try {
            if (server.group.type == ServerGroupType.STATIC) {
                dockerContainerRegistry.dockerClient.stopContainerCmd(server.containerId).exec()
            } else {
                dockerContainerRegistry.dockerClient.killContainerCmd(server.containerId).exec()
            }
            dockerContainerRegistry.dockerClient.removeContainerCmd(server.containerId).exec()
            if (server.group.type == ServerGroupType.DYNAMIC) {
                val dir = File("tmp/${server.id}")
                if (dir.exists()) {
                    FileUtils.deleteDirectory(dir)
                }
            }
            runningServers.remove(server.id)
            println("Server ${server.id} stopped.")

            MasterConnectionService.conn?.send(
                RemovedServerContainerPacket(
                    packetId = "",
                    serverId = server.id
                )
            )
        } catch (e: Exception) {
            println("Error stopping server ${server.id}: ${e.message}")
        }
    }

    fun getRunningServersList(): Collection<RunningServer> = runningServers.values

    fun getServersByGroup(groupName: String): List<RunningServer> =
        runningServers.values.filter { it.group.name == groupName }

    @PreDestroy
    fun onStop() {
        println("Shutting down, stopping all running servers...")
        runningServers.values.toList().forEach { server ->
            println("Stopping server ${server.id}...")
            stopServer(server.id)
        }
        println("All servers stopped.")
    }

}