plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "ab.foxseele.task-tracker-cli"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}

application {
    mainClass.set("ab.foxseele.task-tracker-cli.TaskTrackerKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "ab.foxseele.task-tracker-cli.TaskTrackerKt")
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}