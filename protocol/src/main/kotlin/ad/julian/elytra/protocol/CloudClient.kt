package ad.julian.elytra.protocol

import ad.julian.elytra.protocol.broadcast.BroadcastProxies
import ad.julian.elytra.protocol.types.Proxy
import ad.julian.elytra.protocol.types.RunningServer
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
import java.io.FileInputStream
import java.util.*


open class WebSocketClient(open val url: String = "ws://localhost:8080/ws") {

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    // Callbacks mit Default-Implementierungen
    private var onOpenCallback: (WebSocket) -> Unit = {}
    private var onMessageCallback: (String) -> Unit = {}
    private var onClosingCallback: (code: Int, reason: String) -> Unit = { _, _ -> }
    private var onClosedCallback: (code: Int, reason: String) -> Unit = { _, _ -> }
    private var onFailureCallback: (Throwable) -> Unit = {}

    fun onOpen(callback: (WebSocket) -> Unit) = apply { onOpenCallback = callback }
    fun onMessage(callback: (String) -> Unit) = apply { onMessageCallback = callback }
    fun onClosing(callback: (Int, String) -> Unit) = apply { onClosingCallback = callback }
    fun onClosed(callback: (Int, String) -> Unit) = apply { onClosedCallback = callback }
    fun onFailure(callback: (Throwable) -> Unit) = apply { onFailureCallback = callback }

    fun connect() = apply {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                onOpenCallback(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageCallback(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                onMessageCallback(bytes.utf8())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                onClosingCallback(code, reason)
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                onClosedCallback(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onFailureCallback(t)
            }
        })
    }

    fun send(message: String) {
        if (::webSocket.isInitialized) {
            webSocket.send(message)
        } else {
            println("WebSocket not connected yet!")
        }
    }

    fun close() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "Client closed")
        }
    }

    fun connectAndKeepAlive() {
        connect()
        var i = 3
        while (!::webSocket.isInitialized && i-- > 0) {
            Thread.sleep(1000)
            connectAndKeepAlive()
        }
        onClosed { _, _ ->
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            close()
        })
    }
}

class CloudClient(val address: String = "localhost:8080") : WebSocketClient("ws://${address}/ws"),
    CloudTransportation {

    val httpClient = OkHttpClient.Builder()
        .build()

    override val generalPacketHandlers: MutableList<(Packet, PacketType) -> Unit> =
        mutableListOf()
    override val packetHandlers: MutableMap<PacketType, MutableList<(Packet) -> Unit>> =
        mutableMapOf()

    init {
        onMessage {
            val packet = VelocityProtocol.fromJson(it)
            generalPacketHandlers.forEach { handler -> handler(packet, packet.type) }
            packetHandlers[packet.type]?.forEach { handler -> handler(packet) }
        }
    }

    override fun send(packet: Packet) {
        val json = VelocityProtocol.mapper.writeValueAsString(packet)
        send(json)
    }

    fun <T> request(
        path: String,
        responseType: Class<T>,
        method: String = "GET",
        body: Any? = null,
        conf: ((builder: Request.Builder) -> Void)? = null
    ): T? {

        val requestBody: RequestBody? = if (body == null) null else when (body) {
            is String -> body.toRequestBody()
            else -> {
                val json = VelocityProtocol.mapper.writeValueAsString(body)
                json.toRequestBody()
            }
        }

        val request = Request.Builder()
            .url("http://${address}/${if (path.startsWith("/")) path.substring(1) else path}")
            .method(method, requestBody)

        if (conf != null) {
            conf(request)
        }

        httpClient.newCall(
            request
                .build()
        ).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected code $response")

            val responseBody = response.body?.string() ?: return null
            return VelocityProtocol.mapper.readValue(responseBody, responseType)
        }
    }

    fun getServers(): List<RunningServer>? {
        return request("/servers", Array<RunningServer>::class.java)?.toList()
    }

    fun getProxies(): List<Proxy>? {
        return request("/proxies", Array<Proxy>::class.java)?.toList()
    }

    fun sendToProxies(packet: Packet, proxies: List<String>? = null) {
        send(BroadcastProxies(packet, proxies))
    }

    companion object {
        fun fromConfig(path: String = "elytra.properties"): CloudClient {
            val props = Properties()
            FileInputStream(path).use { input ->
                props.load(input)
            }
            return CloudClient(props["api"].toString())
        }

        fun getInstanceId(path: String = "elytra.properties"): String {
            val props = Properties()
            FileInputStream(path).use { input ->
                props.load(input)
            }
            return props["id"].toString()
        }
    }
}