// ==========================================================================================
//  common —— 跨加载器共享代码
//
//  这里只能使用：原版 Minecraft API、原版自带的库（如 slf4j / gson）、以及两端都提供的
//  Mixin / MixinExtras。任何加载器专有的 API 都会编译失败（这是设计使然）。
//
//  全部构建逻辑在 BuildManager/src/main/kotlin/multiloader.common.gradle.kts
// ==========================================================================================

plugins {
    id("multiloader.common")
}

dependencies {
    // 跨加载器的第三方库写在这里（两端都要能提供它，或用 include/jarInJar 打包）。
    // 例：implementation("com.google.code.gson:gson:2.11.0")
}
