import org.gradle.api.tasks.CopySpec
import org.gradle.api.tasks.bundling.DuplicatesStrategy

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
}

val KOTLIN = "1.5.31"
val JVM = "1.8"
val SERIALIZATION_PLUGIN = "1.5.31"

group = "me.y9san9.prizebot"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    // Add other dependencies here
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JVM
}

val deployPropertiesFile = file("deploy.properties")

if (deployPropertiesFile.exists()) {
    val properties = loadProperties(deployPropertiesFile.absolutePath)

    project.apply<me.y9san9.deploy.Deploy>()
    project.configure<me.y9san9.deploy.DeployConfiguration> {
        serviceName = "prizebot"
        implementationTitle = "prizebot"
        mainClassName = "me.y9san9.prizebot.MainKt"
        host = properties.getProperty("host")
        user = properties.getProperty("user")
        password = properties.getProperty("password")
        deployPath = properties.getProperty("deployPath")
        // On linux should be something like /home/user/.ssh/known_hosts
        // Or Default Allow Any Hosts if this value is not specified,
        // But then MITM may be performed
        knownHostsFile = properties.getProperty("knownHosts")
    }

    task("stop") {
        group = "deploy"

        doLast {
            project.ssh {
                execute("systemctl stop ${project.the<me.y9san9.deploy.DeployConfiguration>().serviceName}")
            }
        }
    }
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
        project.configurations
            .getByName("runtimeClasspath")
            .map { if (it.isDirectory) it else zipTree(it) }
    )
}
