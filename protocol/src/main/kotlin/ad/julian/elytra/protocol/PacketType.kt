package ad.julian.elytra.protocol

import ad.julian.elytra.protocol.broadcast.BroadcastProxies
import ad.julian.elytra.protocol.client.gameactions.MovePlayerPacket
import ad.julian.elytra.protocol.client.instance.UpdateServerInfoPacket
import ad.julian.elytra.protocol.client.instance.UpdateServerStatusPacket
import ad.julian.elytra.protocol.client.proxy.*
import ad.julian.elytra.protocol.nodes.RegisterNodePacket
import ad.julian.elytra.protocol.nodes.proxycontainer.*
import ad.julian.elytra.protocol.nodes.servercontainer.*
import ad.julian.elytra.protocol.server.ProxyRegisterServerPacket
import ad.julian.elytra.protocol.server.ProxyRemoveServerPacket

enum class PacketType(val type: Class<out Packet>) {
    BROADCAST_PROXIES(BroadcastProxies::class.java),

    // BROADCAST_SERVERS(),
    // BROADCAST_NODES(),
    PROXY_REGISTER_SERVER(ProxyRegisterServerPacket::class.java),
    PROXY_REMOVE_SERVER(ProxyRemoveServerPacket::class.java),
    UPDATE_PROXY_STATUS(UpdateProxyStatusPacket::class.java),
    SERVER_REGISTERED_ON_PROXY(ServerRegisteredOnProxyPacket::class.java),
    SERVER_REMOVED_FROM_PROXY(ServerRemovedFromProxyPacket::class.java),
    PUT_PLAYER_PACKET(PutPlayerPacket::class.java),
    REMOVE_PLAYER_PACKET(RemovePlayerPacket::class.java),
    UPDATE_SERVER_STATUS_PACKET(UpdateServerStatusPacket::class.java),
    REGISTER_NODE_PACKET(RegisterNodePacket::class.java),

    CREATE_SERVER_CONTAINER_PACKET(CreateServerContainerPacket::class.java),
    CREATED_SERVER_CONTAINER_PACKET(CreatedServerContainerPacket::class.java),
    START_SERVER_CONTAINER_PACKET(StartServerContainerPacket::class.java),
    STOP_SERVER_CONTAINER_PACKET(StopServerContainerPacket::class.java),
    REMOVED_SERVER_CONTAINER_PACKET(RemovedServerContainerPacket::class.java),

    CREATE_PROXY_CONTAINER_PACKET(CreateProxyContainerPacket::class.java),
    CREATED_PROXY_CONTAINER_PACKET(CreatedProxyContainerPacket::class.java),
    START_PROXY_CONTAINER_PACKET(StartProxyContainerPacket::class.java),
    STOP_PROXY_CONTAINER_PACKET(StopProxyContainerPacket::class.java),
    REMOVED_PROXY_CONTAINER_PACKET(RemovedProxyContainerPacket::class.java),
    MOVE_PLAYER_PACKET(MovePlayerPacket::class.java),
    UPDATE_SERVER_INFO_PACKET(UpdateServerInfoPacket::class.java),


    ;
}