plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    `maven-publish`
}

val mavenVersion = "1.9"

tasks {
    build.get().dependsOn(shadowJar)

    shadowJar {
        finalizedBy(publish)
        archiveFileName.set("memory-store.jar")
        destinationDirectory.set(file("out"))
    }

    publishing {
        repositories {
            mavenLocal()
            if (project.hasProperty("mavenUsername")) {
                maven {
                    credentials {
                        username = project.property("mavenUsername") as String
                        password = project.property("mavenPassword") as String
                    }

                    setUrl("https://repo.codemc.org/repository/maven-releases/")
                }
            }
        }

        publications {
            register("mavenJava", MavenPublication::class) {
                artifact(file("out/memory-store.jar"))

                groupId = "com.oop"
                artifactId = "memory-store"
                version = mavenVersion
            }
        }
    }
}
