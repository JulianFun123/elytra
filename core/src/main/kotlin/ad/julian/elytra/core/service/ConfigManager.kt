package ad.julian.elytra.core.service

import ad.julian.elytra.core.config.GroupsConfig
import ad.julian.elytra.core.config.ProxyConfig
import ad.julian.elytra.core.config.TemplatesConfig
import ad.julian.elytra.core.config.YamlConfig
import org.springframework.stereotype.Component
import java.io.File

@Component
class ConfigManager {
    lateinit var templatesConfig: TemplatesConfig
    lateinit var groupsConfig: GroupsConfig
    lateinit var proxiesConfig: ProxyConfig

    fun <T : YamlConfig> fromFile(filePath: String, clazz: Class<T>): T {
        val file = File(filePath)
        println("Loading config from ${file.absolutePath}")
        if (!file.exists()) {
            val instance = clazz.getDeclaredConstructor().newInstance()
            instance.saveToFile()
            return instance
        }
        return YamlConfig.yaml.readValue(file.readText(), clazz)
    }

    fun loadConfigs() {
        templatesConfig = fromFile("templates.yml", TemplatesConfig::class.java)
        groupsConfig = fromFile("groups.yml", GroupsConfig::class.java)
        proxiesConfig = fromFile("proxies.yml", ProxyConfig::class.java)
    }

    init {
        loadConfigs()
    }

}