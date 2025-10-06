plugins {
    kotlin("jvm") version "2.2.20" apply false
    `maven-publish`
}

group = "ad.julian.elytra"
version = "plugins"

version = System.getenv("RELEASE_TAG") ?: "1.0.0-SNAPSHOT"

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    group = "ad.julian.elytra"
    version = rootProject.version // use root project's version

    repositories {
        mavenCentral()
    }

    publishing {


        publications {
            create<MavenPublication>("maven") {
                from(components["kotlin"]) // important!
                artifactId = project.name
                pom {
                    name.set(project.name)
                    description.set("Kotlin module ${project.name} for Elytra project")
                    url.set("https://elytra.julian.ad")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("julian")
                            name.set("Julian")
                            email.set("julian@gojani.xyz")
                        }
                    }
                }
            }
        }


        repositories {
            maven {
                name = "nexus"

                url = uri(
                    if (version.toString().endsWith("-SNAPSHOT"))
                        "https://registry.intera.dev/repository/maven-snapshots/"
                    else
                        "https://registry.intera.dev/repository/maven-releases/"
                )

                credentials {
                    username = findProperty("nexusUsername") as String? ?: System.getenv("NEXUS_USERNAME")
                    password = findProperty("nexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}

tasks.register("buildAll") {
    dependsOn(":velocity:copyPlugin")
    dependsOn(":core:bootJar")
}
