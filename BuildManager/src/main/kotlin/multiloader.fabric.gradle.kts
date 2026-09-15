import multiloader.LoaderAttribute
import multiloader.modProperty
import multiloader.tagLoaderVariants

// ==========================================================================================
//  multiloader.fabric —— fabric 模块专用（multiloader.loader + Fabric Loom）
// ==========================================================================================

plugins {
    `java-library`
    id("multiloader.loader")
    id("net.fabricmc.fabric-loom")
}

val modId: String = modProperty("mod_id")

dependencies {
    // Minecraft 26.x 起 Fabric 移除了 intermediary、直接使用 Mojang 官方映射，
    // 所以这里既不需要 mappings { }，也可以用普通 implementation 引入 Fabric API。
    add("minecraft", "com.mojang:minecraft:${modProperty("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${modProperty("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${modProperty("fabric_api_version")}")

    // 只给 Fabric 用的其它模组依赖，请在 fabric/build.gradle.kts 里用 modImplementation(...) 追加
}

loom {
    // common 里放了 AccessWidener 时自动接入
    val accessWidener = rootProject.file("common/src/main/resources/$modId.accesswidener")
    if (accessWidener.exists()) {
        accessWidenerPath.set(accessWidener)
    }

    // Loom 默认已经创建了 client / server 两个运行配置，这里用 maybeCreate 兼容“已存在/不存在”两种情况。
    // 使用的是 Loom 1.17+ 的 RunConfiguration API（displayName / generateRunConfig / runDirectory）；
    // 若降级到 Loom 1.16 及以下，请改回 setConfigName(..) / ideConfigGenerated(..) / runDir(..)。
    runs {
        maybeCreate("client").apply {
            client()
            displayName.set("Fabric Client")
            generateRunConfig.set(true)
            runDirectory.set(layout.projectDirectory.dir("runs/client"))
        }
        maybeCreate("server").apply {
            server()
            displayName.set("Fabric Server")
            generateRunConfig.set(true)
            runDirectory.set(layout.projectDirectory.dir("runs/server"))
        }
    }
}

tagLoaderVariants(LoaderAttribute.FABRIC, "includeInternal", "modCompileClasspath")
