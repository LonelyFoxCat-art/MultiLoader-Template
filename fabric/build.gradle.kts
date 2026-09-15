// ==========================================================================================
//  fabric —— Fabric 端模组
//
//  构建逻辑（Loom、运行配置、common 源码合并、变体标记）都在
//  BuildManager/src/main/kotlin/multiloader.fabric.gradle.kts
// ==========================================================================================

plugins {
    id("multiloader.fabric")
}

dependencies {
    // 只有 Fabric 端需要的依赖写在这里；跨加载器的依赖请写到 common/build.gradle.kts
    // 例：modImplementation("maven.modrinth:modmenu:VERSION")
}

// 需要覆盖 Loom 配置时，直接在这里再写一个 loom { } 块即可，例如：
// loom {
//     runs {
//         named("client") {
//             programArguments.addAll("--username", "Dev")
//         }
//     }
//     // 拆分 client / main 源集（可选）：splitEnvironmentSourceSets()
// }
