plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "com.hendy.cybertech"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server Netty (Chạy web server trên Render.com)
    implementation("io.ktor:ktor-server-netty-jvm:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.0")

    // Ktor Client (Gọi Telegram API & Backend Gateway)
    implementation("io.ktor:ktor-client-core-jvm:3.0.0")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.0")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:3.0.0")

    // KotlinX Serialization & Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    
    // Logback cho hệ thống ghi log trên server
    implementation("ch.qos.logback:logback-classic:1.5.8")
}

application {
    mainClass.set("com.hendy.cybertech.bot.ApplicationKt")
}

// Đóng gói thành Fat JAR để deploy dễ dàng trên Render.com
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
