package ad.julian.elytra.core.service

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class ProxyScalerService(val configs: ConfigManager, val proxyRegistry: ProxyRegistry, val nodeRegistry: NodeRegistry) {
    val logger = LoggerFactory.getLogger(ProxyScalerService::class.java)

    var doStart = false

    fun checkAndScale() {
        if (!doStart)  {
            logger.info("Delay start!")
            return
        }
        if (nodeRegistry.nodes.isEmpty()) {
            logger.info("Currently no master node found, skipping scaling check until a master node is found.")
            return
        }

        configs.proxiesConfig.proxies.forEach { proxy ->
            if (proxy.autostart == true && proxyRegistry.runningProxies.values.find { it.proxy.name == proxy.name } == null) {
                val createProxy = proxyRegistry.createProxy(proxy.name)
                proxyRegistry.startProxy(createProxy)
            }
        }
    }

    @EventListener
    fun onServerStart(event: ApplicationReadyEvent) {
        doStart = true
    }

    private val lock = ReentrantLock()

    @EventListener
    fun onWebSocketReady(event: WebServerInitializedEvent) {

    }

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