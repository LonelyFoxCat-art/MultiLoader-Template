plugins {
    // 预编译脚本插件（precompiled script plugins）：
    // src/main/kotlin 下每个 *.gradle.kts 文件都会按其文件名注册成一个插件 id，
    // 例如 multiloader.base.gradle.kts -> id("multiloader.base")
    `kotlin-dsl`
}

description = "多加载器共享构建逻辑（Kotlin DSL 预编译脚本插件）"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.fabricmc.net") { name = "Fabric" }
    maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
}

// 版本集中在 BuildManager/gradle.properties
val loomVersion: String = providers.gradleProperty("loom_version").get()
val modDevVersion: String = providers.gradleProperty("moddev_version").get()

dependencies {
    // 把两个加载器的 Gradle 插件放进 BuildManager 的 classpath，
    // 预编译脚本插件里就能直接写 loom { } / neoForge { }，并获得类型安全与 IDE 补全。
    implementation("net.fabricmc:fabric-loom:$loomVersion")
    implementation("net.neoforged:moddev-gradle:$modDevVersion")
}
