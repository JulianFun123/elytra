package ad.julian.elytra.node.services

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import org.springframework.stereotype.Service

@Service
class DockerContainerRegistry {
    val config: DefaultDockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
        //.withDockerHost("tcp://host.docker.internal:2375")
        .build()


    val dockerClient: DockerClient = DockerClientBuilder.getInstance(config)
        .withDockerHttpClient(ApacheDockerHttpClient.Builder().dockerHost(config.dockerHost).build())
        .build()


    fun createContainerBuilder(
        name: String,
        image: String,
        hostname: String = name,
        env: Map<String, String> = emptyMap(),
        volumes: Map<String, String> = emptyMap(),
        portMapping: Map<String, String> = emptyMap(),
    ): CreateContainerCmd {
        val portsBinding = Ports()

        portMapping.forEach {
            portsBinding.bind(ExposedPort.tcp(it.value.toInt()), Ports.Binding.bindPort(it.key.toInt()))
        }

        val hostConfig = HostConfig.newHostConfig()
            .withBinds(
                volumes.map { Bind.parse("${it.key}:${it.value}") }
            )
            .withPortBindings(portsBinding)

        val container = dockerClient
            .createContainerCmd(image)
            .withName(name)
            .withHostName(hostname)
            .withHostConfig(hostConfig)
            //.withEnv(template.environment?.map { "${it.key}=${it.value}" } ?: emptyList())
            // template.environment?.map { "${it.key}=${it.value}" } ?: emptyList()
            .withEnv(
                env.map { "${it.key}=${it.value}" }
            )

        return container
    }
}