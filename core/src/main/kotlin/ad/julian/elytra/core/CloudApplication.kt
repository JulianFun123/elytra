package ad.julian.elytra.core

import ad.julian.elytra.core.helper.logTable
import ad.julian.elytra.core.service.*
import ad.julian.elytra.core.websocket.WebSocketHandler
import ad.julian.elytra.node.CloudNodeApplication
import ad.julian.elytra.node.services.MasterConnectionService
import ad.julian.elytra.protocol.types.RunningServer
import ad.julian.elytra.protocol.types.ServerGroupType
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import kotlin.system.exitProcess


@SpringBootApplication
@EnableScheduling
class CloudApplication(
    val configManager: ConfigManager,
    val serverRegistry: ServerRegistry,
    val proxyRegistry: ProxyRegistry,
    val nodeRegistry: NodeRegistry,
    val webSocketService: WebSocketService,
    val playerRegistry: CloudPlayerRegistry
) : CommandLineRunner {
    private val commands: MutableMap<String?, (args: Array<String>) -> Unit?> = HashMap()

    override fun run(vararg args: String?) {
        commands["stop"] = {
            exitProcess(0)
        }

        commands["reload"] = {
            configManager
                .loadConfigs()
        }

        fun logServers(servers: List<RunningServer>) {
            logTable(
                listOf("ID", "Group", "Status", "Players", "Labels"),
                servers.map { server ->
                    listOf(
                        server.id,
                        server.group.name,
                        server.status.toString(),
                        "${server.info.pingPlayerCount}/${server.info.pingMaxPlayerCount}",
                        server.labels?.map { "${it.key}=${it.value}" }?.joinToString(",") ?: ""
                    )
                }
            )
        }

        commands["get"] = { args: Array<String> ->
            when (args[0]) {
                "groups" -> {
                    logTable(
                        listOf("Name", "Type", "Template", "Min", "Max", "Autostart", "CheckInterval", "UpscaleRules"),
                        configManager.groupsConfig.groups.map { group ->
                            listOf(
                                group.name,
                                group.type.name,
                                group.templateName,
                                group.lifecycle?.scaling?.min?.toString() ?: "n/a",
                                group.lifecycle?.scaling?.max?.toString() ?: "n/a",
                                if (group.type == ServerGroupType.STATIC) group.lifecycle?.autostart?.toString()
                                    ?: "false" else ("n/a"),
                                if (group.type == ServerGroupType.DYNAMIC) group.lifecycle?.scaling?.checkIntervalSeconds?.toString()
                                    ?: "n/a" else ("n/a"),
                                if (group.type == ServerGroupType.DYNAMIC) group.lifecycle?.scaling?.upscale?.size?.toString()
                                    ?: "0" else ("n/a")
                            )
                        }
                    )
                }

                "templates" -> {
                    val templates = configManager.templatesConfig

                    logTable(
                        listOf("Name", "Image", "Memory", "Type", "Labels"),
                        templates.templates.map { template ->
                            listOf(
                                template.name,
                                template.image,
                                template.memory.toString(),
                                template.type,
                            )
                        }
                    )
                }

                "proxies" -> {
                    logTable(
                        listOf("Name", "Template", "Port"),
                        configManager.proxiesConfig.proxies.map { proxy ->
                            listOf(
                                proxy.name,
                                proxy.templateName,
                                proxy.port.toString(),
                            )
                        }
                    )
                }

                "servers" -> {
                    logServers(serverRegistry.runningServers.values.toList())
                }

                "nodes" -> {
                    logTable(
                        listOf("ID"),
                        nodeRegistry.nodes.map { (_, node) ->
                            listOf(
                                node.id,
                            )
                        }
                    )
                }

                "players" -> {
                    logTable(
                        listOf("Username", "UUID", "Current Server", "Ping", "Online Mode"),
                        playerRegistry.players.values.map { player ->
                            listOf(
                                player.userName,
                                player.uuid,
                                player.currentServerId ?: "n/a",
                                player.ping.toString(),
                                player.onlineMode.toString()
                            )
                        }
                    )
                }

                else -> {
                    println("Unknown config: ${args[0]}")
                }
            }
        }

        commands["group"] = { args: Array<String> ->
            val groupName = args[0]
            val group = configManager.groupsConfig.groups.find { it.name == groupName }
            if (group != null) {
                when (args[1]) {
                    "create" -> {
                        try {
                            val server = serverRegistry.createServer(groupName)
                            println("Created server ${server.id} in group $groupName")
                        } catch (e: Exception) {
                            println("Failed to create server in group $groupName: ${e.message}")
                        }
                    }

                    "servers" -> {
                        logServers(
                            serverRegistry.getServersByGroup(groupName).toList()
                        )
                    }

                    else -> {
                        println("Unknown action: ${args[1]}")
                    }
                }
            } else {
                println("Group $groupName not found")
            }
        }

        commands["server"] = { args: Array<String> ->
            val serverId = args[0]
            val server = serverRegistry.runningServers[serverId]
            if (server != null) {
                when (args[1]) {
                    "start" -> {
                        try {
                            serverRegistry.startServer(serverId)
                            println("Started server $serverId")
                        } catch (e: Exception) {
                            println("Failed to start server $serverId: ${e.message}")
                        }
                    }

                    "stop" -> {
                        try {
                            serverRegistry.stopServer(serverId)
                            println("Stopped server $serverId")
                        } catch (e: Exception) {
                            println("Failed to stop server $serverId: ${e.message}")
                        }
                    }

                    else -> {
                        println("Unknown action: ${args[1]}")
                    }
                }
            } else {
                println("Server $serverId not found")
            }
        }

        commands["proxy"] = { args: Array<String> ->
            val proxyName = args[0]
            val proxyConfig = configManager.proxiesConfig.proxies.find { it.name == proxyName }
            if (proxyConfig != null) {
                when (args[1]) {
                    "servers" -> {
                        logServers(
                            proxyRegistry.runningProxies.values
                                .map { it.registeredServers }
                                .flatten()
                                .map { serverRegistry.runningServers[it] }
                                .filter { it != null }
                                .map { it!! }
                                .toList())
                    }
                }
            }
        }




        Thread { startConsole() }.start()
    }


    private fun startConsole() {
        val scanner: Scanner = Scanner(System.`in`)
        println("Console ready. Type commands:")

        while (true) {
            print("> ")
            val input: String = scanner.nextLine().trim()

            val split = input.split(" ")

            val command = commands[split[0]]
            if (command != null) {
                command(split.drop(1).toTypedArray())
            } else {
                println("Unknown command: $input")
            }
        }
    }
}

fun main(args: Array<String>) {
    val ctx = runApplication<CloudApplication>(*args)
    val webSocketHandler = ctx.getBean(WebSocketHandler::class.java)
    val masterNodeTransportation = MasterNodeTransportation(webSocketHandler)
    MasterConnectionService.conn = masterNodeTransportation
    ctx.getBean(WebSocketService::class.java).sessions.add(masterNodeTransportation.wrappedSession)

    val node = SpringApplication(CloudNodeApplication::class.java)
    node.webApplicationType = WebApplicationType.NONE
    node.run()

    Runtime.getRuntime().addShutdownHook(Thread {
        println("JVM shutdown hook called!")
    })
}
