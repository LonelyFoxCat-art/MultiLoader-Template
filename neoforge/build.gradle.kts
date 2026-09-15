// ==========================================================================================
//  neoforge —— NeoForge 端模组
//
//  构建逻辑（ModDevGradle、client/server/data 运行配置、common 源码合并、变体标记）都在
//  BuildManager/src/main/kotlin/multiloader.neoforge.gradle.kts
// ==========================================================================================

plugins {
    id("multiloader.neoforge")
}

dependencies {
    // 只有 NeoForge 端需要的依赖写在这里；跨加载器的依赖请写到 common/build.gradle.kts
    // 例：implementation("net.neoforged:jei-1.21.x-neoforge:VERSION")
}

// 需要覆盖 ModDevGradle 配置时，直接在这里再写一个 neoForge { } 块即可，例如：
// neoForge {
//     runs {
//         named("client") {
//             programArgument("--username=Dev")
//         }
//     }
//     parchment {
//         minecraftVersion = "26.1"
//         mappingsVersion = "yyyy.mm.dd"
//     }
// }
