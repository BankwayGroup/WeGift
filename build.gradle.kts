import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import me.y9san9.deploy.Deploy
import me.y9san9.deploy.DeployConfiguration
import me.y9san9.deploy.ssh
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.gradle.api.tasks.CopySpec
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.DuplicatesStrategy

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
}

val utils = "your:utils:version"
val bot = "your:bot:version"
val coroutines = "your:coroutines:version"

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

tasks.withType<KotlinCompile> {
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

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
