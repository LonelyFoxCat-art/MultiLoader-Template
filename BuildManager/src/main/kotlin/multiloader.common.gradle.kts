import multiloader.LoaderAttribute
import multiloader.modProperty
import multiloader.tagLoaderVariants

// ==========================================================================================
//  multiloader.common —— common 模块专用
//  · 用 ModDevGradle 的 vanilla(NeoForm) 模式提供“纯净 Minecraft”编译环境，
//    任何加载器专有的 API 都会在这里编译失败，从而保证 common 代码真正跨加载器
//  · 以“源码目录 + 资源目录”的形式把产物暴露给 fabric / neoforge
// ==========================================================================================

plugins {
    `java-library`
    id("multiloader.base")
    id("net.neoforged.moddev")
}

neoForge {
    // vanilla 模式：只给 Minecraft，不给 NeoForge
    neoFormVersion = modProperty("neo_form_version")

    // 存在 AccessTransformer 时自动启用（fabric 端对应的 AccessWidener 见 multiloader.fabric）
    val accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (accessTransformer.exists()) {
        accessTransformers.from(accessTransformer.absolutePath)
    }
}

dependencies {
    // Mixin / MixinExtras 只在编译期需要，两个加载器运行时都自带
    compileOnly("org.spongepowered:mixin:${modProperty("mixin_version")}")
    compileOnly("io.github.llamalad7:mixinextras-common:${modProperty("mixinextras_version")}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${modProperty("mixinextras_version")}")
}

// 把源码目录与资源目录声明为可消费产物，供加载器模块合并
val commonJava = configurations.create("commonJava") {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources = configurations.create("commonResources") {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val mainSourceSet: SourceSet = sourceSets.named("main").get()

artifacts {
    add(commonJava.name, mainSourceSet.java.sourceDirectories.singleFile)
    add(commonResources.name, mainSourceSet.resources.sourceDirectories.singleFile)
}

tagLoaderVariants(LoaderAttribute.COMMON)
