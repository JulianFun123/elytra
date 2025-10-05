plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.ktor:ktor-client-core:2.3.5")
    api("io.ktor:ktor-client-cio:2.3.5")
    api("com.squareup.okhttp3:okhttp:4.11.0") // OkHttp

    api("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    api("com.fasterxml.jackson.core:jackson-core:2.18.0")
    api("com.fasterxml.jackson.core:jackson-annotations:2.18.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
