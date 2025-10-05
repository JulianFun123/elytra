package ad.julian.elytra.core.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


abstract class YamlConfig(@Transient private val file: File) {
    companion object {
        var yaml: ObjectMapper = ObjectMapper(YAMLFactory())

        init {
            yaml.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            yaml.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
        }
    }

    fun saveToYaml(): String {
        return yaml.writeValueAsString(this)
    }

    fun saveToFile() {
        file.writeText(saveToYaml())
    }
}