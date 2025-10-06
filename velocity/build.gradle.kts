plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.10"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation(project(":protocol"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}


tasks.register<Copy>("copyPlugin") {
    print("Copying plugin to deployment folder...")
    from(tasks.named("shadowJar"))
    // With name velocity.jar
    rename { "velocity-all.jar" }

    into(file("${rootDir}/core/src/main/resources/plugins"))
}