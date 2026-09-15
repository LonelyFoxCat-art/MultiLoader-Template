import multiloader.jsonTemplateProperties
import multiloader.modProperty
import multiloader.templateProperties
import org.gradle.external.javadoc.StandardJavadocDocletOptions

// ==========================================================================================
//  multiloader.base —— 所有模块（common / fabric / neoforge）共享的基础配置
//  · Java 工具链、产物命名、LICENSE 打包、manifest 属性
//  · 资源模板变量注入（fabric.mod.json / neoforge.mods.toml / *.mixins.json / pack.mcmeta）
//  · maven-publish
// ==========================================================================================

plugins {
    `java-library`
    `maven-publish`
}

val modId: String = modProperty("mod_id")
val modName: String = modProperty("mod_name")
val modAuthor: String = modProperty("mod_author")
val minecraftVersion: String = modProperty("minecraft_version")

base {
    // 产物名形如 examplemod-fabric-26.1-26.1.0.0.jar
    archivesName.set("$modId-${project.name}-$minecraftVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(modProperty("java_version").toInt()))
    withSourcesJar()
    withJavadocJar()
}

// 统一 UTF-8：源码里有中文注释、资源模板里有中文描述时不会乱码（尤其是 Windows）
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // NeoForge 的事件/注册相关反射依赖方法参数名，建议保留
    options.compilerArgs.add("-parameters")
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    // 模板示例不追求完整 Javadoc，关掉 doclint 以免告警刷屏
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

repositories {
    mavenCentral()
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
    exclusiveContent {
        forRepository {
            maven {
                name = "Sponge"
                url = uri("https://repo.spongepowered.org/repository/maven-public")
            }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_$modName" }
    }

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modName,
                "Specification-Vendor" to modAuthor,
                "Specification-Version" to archiveVersion.get(),
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion.get(),
                "Implementation-Vendor" to modAuthor,
                "Built-On-Minecraft" to minecraftVersion,
            )
        )
    }
}

tasks.named<Jar>("sourcesJar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_$modName" }
    }
}

tasks.processResources {
    val properties = templateProperties()
    val jsonProperties = jsonTemplateProperties()

    // 资源模板一律按 UTF-8 过滤，避免中文描述在不同平台上变成乱码
    filteringCharset = "UTF-8"

    filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(properties)
    }

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
        expand(jsonProperties)
    }

    filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(properties)
    }

    inputs.properties(properties)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    // 只有设置了 local_maven_url 环境变量时才注册发布仓库，
    // 例如：local_maven_url=file:///path/to/maven ./gradlew publish
    val localMavenUrl = providers.environmentVariable("local_maven_url").orNull
    if (!localMavenUrl.isNullOrBlank()) {
        repositories {
            maven {
                name = "local"
                url = uri(localMavenUrl)
            }
        }
    }
}
