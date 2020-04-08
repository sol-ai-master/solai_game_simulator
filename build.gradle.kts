import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
}


group = "org.solai"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("file://${projectDir}/../solai_maven_repo") }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("redis.clients:jedis:3.2.0")

    implementation("io.github.microutils:kotlin-logging:1.7.9")
//    implementation("org.slf4j:slf4j-simple:1.7.26")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation("com.github.sol-ai:sol_champ:-SNAPSHOT")
//    implementation("org.solai:sol_game:1.0")
//
//    val lwjglNatives = "natives-windows"
//    val lwjgl_version = "3.2.3"
//    val natives by configurations.creating
//    listOf("", "-glfw", "-opengl", "-stb").forEach {
//        println("Loading $lwjglNatives for lib: $it")
//        natives("org.lwjgl:lwjgl$it:${lwjgl_version}:$lwjglNatives")
//    }
//    configurations["runtimeOnly"].extendsFrom(natives)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

//application {
//    // Define the main class for the application.
//    mainClassName = "org.solai.solai_game_simulator.AppKt"
//    this.applicationDefaultJvmArgs = listOf("-Dorg.slf4j.simpleLogger.defaultLogLevel=debug")
//}
