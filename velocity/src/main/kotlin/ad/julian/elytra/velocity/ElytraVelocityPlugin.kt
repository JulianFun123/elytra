package ad.julian.elytra.velocity

import ad.julian.elytra.protocol.CloudClient
import ad.julian.elytra.protocol.client.gameactions.MovePlayerPacket
import ad.julian.elytra.protocol.client.instance.UpdateServerInfoPacket
import ad.julian.elytra.protocol.client.instance.UpdateServerStatusPacket
import ad.julian.elytra.protocol.client.proxy.*
import ad.julian.elytra.protocol.server.ProxyRegisterServerPacket
import ad.julian.elytra.protocol.server.ProxyRemoveServerPacket
import ad.julian.elytra.protocol.types.CloudPlayer
import ad.julian.elytra.protocol.types.ServerGroup
import ad.julian.elytra.protocol.types.ServerStatus
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.event.proxy.server.ServerRegisteredEvent
import com.velocitypowered.api.event.proxy.server.ServerUnregisteredEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ConnectionRequestBuilder
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import org.slf4j.Logger
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@Plugin(id = "elytra-velocity", name = "ElytraVelocity", version = "1.0.0", authors = ["Julian"])
class ElytraVelocityPlugin @Inject constructor(val server: ProxyServer, val logger: Logger) {

    var cloudClient: CloudClient? = null
    var id: String? = null

    val serverGroups = mutableMapOf<String, ServerGroup>()

    init {
        instance = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        logger.info("Elytra Velocity Plugin initialized!")
        cloudClient = CloudClient.fromConfig()
        id = CloudClient.getInstanceId()
        logger.info("Instance ID: $id, connecting to cloud server...")

        server.allServers.forEach { server ->
            logger.info("Found registered default server ${server.serverInfo.name}, unregistering...")
            this.server.unregisterServer(server.serverInfo)
        }

        cloudClient!!.onOpen {
            logger.info("Connection to cloud server established!")
            cloudClient!!.send(UpdateProxyStatusPacket(id!!, ServerStatus.RUNNING))
            logger.info("Update Status packet sent!")
        }
        cloudClient!!.onClosed { _, _ ->
            logger.warn("Connection to cloud server lost!")
            cloudClient!!.connect()
        }

        cloudClient!!.onPacket(ProxyRegisterServerPacket::class.java) { packet ->
            logger.info("Register packet for server ID ${packet.serverId} received...")
            if (packet.proxyId != id) return@onPacket
            val server1 = server.getServer(packet.serverId)
            if (server1.isPresent) {
                logger.info("Server with ID ${packet.serverId} is already registered, skipping...")
                return@onPacket
            }

            logger.info("Registering server with ID ${packet.serverId} at ${packet.address}:${packet.port}")
            serverGroups[packet.serverId] = packet.group
            server.registerServer(ServerInfo(packet.serverId, InetSocketAddress(packet.address, packet.port)))
        }

        cloudClient!!.onPacket(ProxyRemoveServerPacket::class.java) { packet ->
            logger.info("Unregister packet for server ID ${packet.serverId} received...")
            //if (packet.proxyId != id) return@onPacket
            server.getServer(packet.serverId).ifPresent { registeredServer ->
                logger.info("Unregister ${packet.serverId}...")
                serverGroups.remove(packet.serverId)
                server.unregisterServer(registeredServer.serverInfo)
            }
        }

        cloudClient!!.onPacket(MovePlayerPacket::class.java) { packet ->
            val player = server.getPlayer(UUID.fromString(packet.playerUUID)).orElse(null) ?: return@onPacket
            val targetServer = server.getServer(packet.targetServer).orElse(null) ?: return@onPacket
            logger.info("Found player ${player.username} and target server ${targetServer.serverInfo.name}, connecting...")
            player.createConnectionRequest(targetServer).fireAndForget()

            player.createConnectionRequest(targetServer).connect()
                .thenAccept(Consumer { result: ConnectionRequestBuilder.Result? ->
                    when (result!!.status) {
                        ConnectionRequestBuilder.Status.SUCCESS -> logger.info("Player ${player.username} connected to ${targetServer.serverInfo.name}")
                        ConnectionRequestBuilder.Status.ALREADY_CONNECTED -> logger.info("Player ${player.username} already connected to server ${targetServer.serverInfo.name}")
                        ConnectionRequestBuilder.Status.CONNECTION_IN_PROGRESS -> logger.info("Player ${player.username} connection in progress server ${targetServer.serverInfo.name}")
                        else -> logger.warn(
                            "Could not connect player ${player.username} to server ${targetServer.serverInfo.name}: ${result.status}"
                        )
                    }
                }).exceptionally { ex ->
                    logger.error("Error connecting player ${player.username} to server ${targetServer.serverInfo.name}: ${ex.message}")
                    null
                }
        }

        logger.info("Connecting to the cloud client! ${cloudClient!!.url}")
        cloudClient!!.connect()
        //server.eventManager.register(this, PlayerJoinAndLeaveListener())

        server.scheduler.buildTask(this, Runnable {
            cloudClient?.send(UpdateProxyStatusPacket(id!!, ServerStatus.RUNNING))

            server.allServers.forEach { server ->
                val start = System.currentTimeMillis()
                server.ping().thenAccept { successful ->
                    val pingTime: Long = System.currentTimeMillis() - start

                    cloudClient?.send(
                        UpdateServerStatusPacket(
                            server.serverInfo.name,
                            ServerStatus.RUNNING,
                        )
                    )
                    val pingPlayers = successful.players.get()

                    cloudClient?.send(
                        UpdateServerInfoPacket(
                            server.serverInfo.name,
                            ad.julian.elytra.protocol.types.ServerInfo(
                                proxyPlayers = server.playersConnected.map { it.uniqueId.toString() },
                                proxyPlayerCount = server.playersConnected.size,
                                pingPlayerCount = pingPlayers.online,
                                pingSamplePlayers = pingPlayers.sample?.associate { it.id to it.name }?.toMutableMap()
                                    ?: mutableMapOf(),
                                pingMaxPlayerCount = pingPlayers.max,
                                pingDescription = successful.descriptionComponent.toString(),
                                pingTime = pingTime,
                                pingVersionName = successful.version.name,
                                pingVersionProtocol = successful.version.protocol,
                            )
                        )
                    )
                }.exceptionally { ex ->
                    logger.warn("Server ${server.serverInfo.name} is not reachable: ${ex.message}")
                    cloudClient?.send(
                        UpdateServerStatusPacket(
                            server.serverInfo.name,
                            ServerStatus.UNRESPONSIVE,
                        )
                    )

                    null
                }
            }

        }).repeat(5, TimeUnit.SECONDS).delay(5, TimeUnit.SECONDS).schedule()
    }

    @Subscribe
    fun onLogin(event: PlayerChooseInitialServerEvent) {
        val randomOrNull = serverGroups.filter { it.value.lobby }.map { Pair(it.key, it.value) }.randomOrNull()
        if (randomOrNull == null) return
        val server = server.getServer(randomOrNull.first)
        if (server.isPresent) {
            event.setInitialServer(server.get())
        }
    }

    @Subscribe
    fun onServerRegistered(event: ServerRegisteredEvent) {
        logger.info("EVENT Registered ${event.registeredServer.serverInfo.name}")
        cloudClient?.send(ServerRegisteredOnProxyPacket(id!!, event.registeredServer.serverInfo.name))
    }

    @Subscribe
    fun onServerRemoved(event: ServerUnregisteredEvent) {
        logger.info("EVENT Unregistered ${event.unregisteredServer.serverInfo.name}")
        cloudClient?.send(ServerRemovedFromProxyPacket(id!!, event.unregisteredServer.serverInfo.name))
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        cloudClient?.send(UpdateProxyStatusPacket(id!!, ServerStatus.STOPPING))
    }

    @Subscribe
    fun playerChangesServer(event: ServerConnectedEvent) {
        cloudClient?.send(PutPlayerPacket(cloudPlayerByPlayer(event.player, event.server.serverInfo)))
    }

    @Subscribe
    fun playerConnect(event: LoginEvent) {
        cloudClient?.send(PutPlayerPacket(cloudPlayerByPlayer(event.player)))
    }

    @Subscribe
    fun playerDisconnects(event: DisconnectEvent) {
        cloudClient?.send(RemovePlayerPacket(event.player.uniqueId.toString()))
    }

    fun cloudPlayerByPlayer(player: Player, server: ServerInfo? = null): CloudPlayer {
        return CloudPlayer(
            proxy = id,
            userName = player.username,
            uuid = player.uniqueId.toString(),
            currentServerId = server?.name ?: player.currentServer.map { it.serverInfo.name }.orElse(null),
            ping = player.ping,
            onlineMode = player.isOnlineMode
        )
    }

    companion object {
        lateinit var instance: ElytraVelocityPlugin
            private set
    }

}