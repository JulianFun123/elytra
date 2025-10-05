package ad.julian.elytra.node

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CloudNodeApplication

fun main(args: Array<String>) {
    runApplication<CloudNodeApplication>(*args)
}