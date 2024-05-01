import me.y9san9.deploy.Deploy
import me.y9san9.deploy.DeployConfiguration
import me.y9san9.deploy.ssh
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
}

val utils = "your:utils:version"
val bot = "your:bot:version"
val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"

group = "your.group"
version = "your.version"

repositories {
    mavenCentral()
}

dependencies {
    implementation(utils)
    implementation(bot)
    implementation(coroutines)
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val deployPropertiesFile = file("deploy.properties")

if (deployPropertiesFile.exists()) {
    val properties = loadProperties(deployPropertiesFile.absolutePath)

    project.apply<Deploy>()
    project.configure<DeployConfiguration> {
        serviceName = "prizebot"
        implementationTitle = "prizebot"
        mainClassName = "me.y9san9.prizebot.MainKt"
        host = properties.getProperty("host")
        user = properties.getProperty("user")
        password = properties.getProperty("password")
        deployPath = properties.getProperty("deployPath")
        knownHostsFile = properties.getProperty("knownHosts")
    }

    task("stop") {
        group = "deploy"

        doLast {
            project.ssh {
                execute("systemctl stop ${project.the<DeployConfiguration>().serviceName}")
            }
        }
    }
}

tasks.register("stage") {
    dependsOn("build")
}

val fatJar by tasks.creating(Jar::class) {
    dependsOn("build")

    // Removed duplicatesStrategy as it's not necessary
    group = "build"
    archiveFileName.set("app.jar")

    manifest {
        attributes["Implementation-Title"] = "Prizebot"
        attributes["Main-Class"] = "me.y9san9.prizebot.MainKt"
    }

    from(
        project.configurations.getByName("runtimeClasspath")
            .map { if (it.isDirectory) it else zipTree(it) }
    )
}
