package ad.julian.elytra.core.service

import ad.julian.elytra.protocol.server.ProxyRegisterServerPacket
import ad.julian.elytra.protocol.types.RunningServer
import ad.julian.elytra.protocol.types.ServerGroupType
import ad.julian.elytra.protocol.types.ServerStatus
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class ServerScalerService(
    val configs: ConfigManager,
    val serverRegistry: ServerRegistry,
    val proxyRegistry: ProxyRegistry,
    val webSocketService: WebSocketService,
) {
    val logger = LoggerFactory.getLogger(ServerScalerService::class.java)

    var seconds = 0

    fun checkAndScale() {
        if (proxyRegistry.runningProxies.values.none { it.status == ServerStatus.RUNNING }) {
            // No proxies are running, skip scaling
            logger.info("No running proxies found, skipping scaling check until a running instance is found.")
            return
        }

        configs.groupsConfig.groups.forEach { group ->
            fun getServers(): List<RunningServer> = serverRegistry.getServersByGroup(group.name)

            //logger.info("Checking group ${group.name} with ${servers.size} servers.")
            if (group.type == ServerGroupType.STATIC) {
                if (group.lifecycle?.autostart == true) {
                    if (getServers().isEmpty()) {
                        try {
                            logger.info("Autostarting server for group ${group.name}")
                            val createServer = serverRegistry.createServer(group.name)
                            serverRegistry.startServer(createServer.id)
                        } catch (e: Exception) {
                            logger.error("Failed to create or start server for group ${group.name}: ${e.message}")
                        }
                    }
                }
            } else if (group.type == ServerGroupType.DYNAMIC) {

                val scaling = group.lifecycle?.scaling

                if (scaling != null && seconds % scaling.checkIntervalSeconds == 0) {
                    var createServers = 0
                    while (scaling.min > getServers().size + createServers) {
                        createServers++
                    }

                    scaling.upscale.forEach { scaleRule ->
                        if (scaleRule.playerCount != null) {
                        }
                        if (scaleRule.script != null) {
                            //
                        }
                    }


                    if (getServers().size + createServers > scaling.max) {
                        createServers = scaling.max - getServers().size
                    }

                    if (createServers > 0) {
                        logger.info("Decided to scale up group ${group.name} by $createServers servers.")

                        for (i in 1..createServers) {
                            try {
                                logger.info("Scaling up group ${group.name}: ${getServers().size} -> ${getServers().size + 1}")
                                val createServer = serverRegistry.createServer(group.name)
                                serverRegistry.startServer(createServer.id)
                            } catch (e: Exception) {
                                logger.error("Failed to create or start server for group ${group.name}: ${e.message}")
                            }
                        }
                    } else if (createServers < 0) {
                        logger.info("Decided to scale down group ${group.name} by ${-createServers} servers.")

                        for (i in createServers until 0) {
                            val servers = getServers()
                            if (servers.size <= scaling.min) break
                            val serverToStop =
                                servers.minBy { (it.info.pingPlayerCount / it.info.pingMaxPlayerCount) /*+ (it.info.cpuLoad * 4) + (it.info.usedMemory.toDouble() / it.info.maxMemory)*/ }
                            try {
                                logger.info("Scaling down group ${group.name}: ${servers.size} -> ${servers.size - 1}")
                                serverRegistry.stopServer(serverToStop.id)
                            } catch (e: Exception) {
                                logger.error("Failed to stop server ${serverToStop.id} for group ${group.name}: ${e.message}")
                            }
                        }
                    }
                }
            }

            //logger.info("Group ${group.name} has ${getServers().size} servers.")
            proxyRegistry.runningProxies.values.forEach { proxy ->
                //logger.info("Checking proxy ${proxy.proxy.name} for group ${group.name}.")
                if (proxy.proxy.groups.contains(group.name)) {
                    getServers().forEach { server ->
                        if (!proxy.registeredServers.contains(server.id)) {
                            logger.info("Registering server ${server.id} on proxy ${proxy.proxy.name}.")
                            webSocketService.broadcast(
                                ProxyRegisterServerPacket(
                                    proxy.id,
                                    server.id,
                                    server.id,
                                    25565,
                                    server.group
                                )
                            )
                        }
                        // else if (proxy.registeredServers.contains(server.id)) {
                        //   webSocketService.broadcast(ProxyRemoveServerPacket(proxy.id, server.id))
                        // }
                    }
                    // Remove all servers that are no longer part of the group

                    proxy.registeredServers.forEach { server ->
                        /*if (servers.none { it.id == server && it.group.name == group.name }) {
                            webSocketService.broadcast(ProxyRemoveServerPacket(proxy.id, server))
                        } */
                    }
                }
            }
        }


        seconds++
    }

    @EventListener
    fun onServerStart(event: ApplicationReadyEvent) {
        println("Server started: ${event.timeTaken}")
    }

    private val lock = ReentrantLock()

    @Scheduled(fixedRate = 1000)
    fun scheduler() {
        if (lock.tryLock()) {
            try {
                checkAndScale()
            } finally {
                lock.unlock()
            }
        } else {
            logger.warn("Scheduler is already running, skipping this tick.")
        }
    }
}