package ad.julian.elytra.core.config

import ad.julian.elytra.protocol.types.Proxy
import ad.julian.elytra.protocol.types.ProxyTemplate
import ad.julian.elytra.protocol.types.ProxyType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
class ProxyConfig : YamlConfig(File("proxies.yml")) {
    var proxies: MutableList<Proxy> = mutableListOf(
        Proxy(
            name = "proxy",
            templateName = "default-proxy",
            autostart = true,
        )
    )
    var templates: MutableList<ProxyTemplate> = mutableListOf(
        ProxyTemplate(
            name = "default-proxy",
            dir = "proxies/default-proxy",
            type = ProxyType.VELOCITY,
            version = "LATEST",
            port = 25565,
            memory = 1024,
            groups = mutableListOf("basewars"),
            network = "mcserver"
        )
    )
}