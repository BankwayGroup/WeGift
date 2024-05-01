import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.gradle.api.tasks.CopySpec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.DuplicatesStrategy

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
}

val Version = object {
    const val KOTLIN = "1.5.31"
    const val JVM = "1.8"
    const val SERIALIZATION_PLUGIN = "1.5.31"
}

val AppInfo = object {
    const val PACKAGE = "me.y9san9.prizebot"
    const val VERSION = "1.0"
}

val utils = "me.y9san9.prizebot:utils:1.0"
val bot = "me.y9san9.prizebot:bot:1.0"
val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"

group = AppInfo.PACKAGE
version = AppInfo.VERSION

repositories {
    mavenCentral()
}

dependencies {
    implementation(utils)
    implementation(bot)
    implementation(coroutines)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Version.JVM
}

val deployPropertiesFile = file("deploy.properties")

if (deployPropertiesFile.exists()) {
    val properties = loadProperties(deployPropertiesFile.absolutePath)

    apply<me.y9san9.deploy.Deploy>()
    configure<me.y9san9.deploy.DeployConfiguration> {
        serviceName = "prizebot"
        implementationTitle = "prizebot"
        mainClassName = "me.y9san9.prizebot.MainKt"
        host = properties.getProperty("host")
        user = properties.getProperty("user")
        password = properties.getProperty("password")
        deployPath = properties.getProperty("deployPath")
        knownHostsFile = properties.getProperty("knownHosts")
    }

    tasks.register("stop") {
        group = "deploy"
        doLast {
            ssh {
                execute("systemctl stop ${project.the<me.y9san9.deploy.DeployConfiguration>().serviceName}")
            }
        }
    }
}

tasks.register<Jar>("fatJar") {
    dependsOn("build")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    group = "build"
    archiveFileName.set("app.jar")

    manifest {
        attributes["Implementation-Title"] = "Prizebot"
        attributes["Main-Class"] = "me.y9san9.prizebot.MainKt"
    }

    from(
        configurations.getByName("runtimeClasspath")
            .map { if (it.isDirectory) it else zipTree(it) }
    )
}
