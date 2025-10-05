package ad.julian.elytra.node.helper

import java.io.File
import java.io.FileOutputStream
import java.util.*

fun createInstanceConfig(id: String, file: File) {
    val elytraConfig = Properties()
    elytraConfig["id"] = id
    elytraConfig["api"] = "host.docker.internal:8080"
    elytraConfig["key"] = "x"
    FileOutputStream(file).use { stream ->
        elytraConfig.store(stream, "Elytra Internal Config")
    }
}

fun movePluginToInstance(pluginName: String, instanceDir: File, clazz: Class<*>) {
    val pluginsDir = File(instanceDir, "plugins")
    pluginsDir.mkdirs()
    val pluginFile = File(pluginsDir, pluginName)
    val inputStream = clazz.classLoader.getResourceAsStream("plugins/$pluginName")
    if (inputStream != null) {
        FileOutputStream(pluginFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    } else {
        throw IllegalStateException("Plugin $pluginName not found in resources.")
    }
}