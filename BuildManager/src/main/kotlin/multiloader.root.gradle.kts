import multiloader.jsonTemplateProperties

// ==========================================================================================
//  multiloader.root —— 根项目约定
//  · buildAllMods    ：一次构建三个模块
//  · printEnvironment：打印当前模板的环境信息，便于排查版本问题
// ==========================================================================================

plugins {
    base
}

tasks.register("buildAllMods") {
    group = "multiloader"
    description = "构建 common / fabric / neoforge 三个模块的 jar"
    // 用任务路径字符串而不是 project(":fabric").tasks，避免跨项目配置
    dependsOn(":common:build", ":fabric:build", ":neoforge:build")
}

tasks.register("printEnvironment") {
    group = "multiloader"
    description = "打印 Minecraft / 加载器版本，以及注入到资源模板中的属性"

    val properties = jsonTemplateProperties()
    val gradleVersion = gradle.gradleVersion
    val javaVersion = System.getProperty("java.version")

    doLast {
        logger.lifecycle("── MultiLoader-Template ─────────────────────────────")
        logger.lifecycle("Gradle            : $gradleVersion")
        logger.lifecycle("Java (Gradle JVM) : $javaVersion")
        logger.lifecycle("构建插件版本      : 见 BuildManager/gradle.properties")
        logger.lifecycle("── 注入到资源模板中的属性 ───────────────────────────")
        properties.forEach { (key, value) -> logger.lifecycle("  $key = $value") }
        logger.lifecycle("─────────────────────────────────────────────────────")
    }
}
