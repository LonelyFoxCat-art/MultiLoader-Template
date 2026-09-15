import multiloader.LoaderAttribute

// ==========================================================================================
//  multiloader.loader —— fabric / neoforge 两个加载器模块共享的配置
//  把 common 的“源码 + 资源”合并进本模块，使最终 jar 自带跨加载器代码
// ==========================================================================================

plugins {
    `java-library`
    id("multiloader.base")
}

// 用于解析 common 暴露出来的源码目录 / 资源目录
val commonJava = configurations.create("commonJava") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val commonResources = configurations.create("commonResources") {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    // common 只参与编译，不打进依赖清单；用 loader 属性指定要消费的变体
    // 注意：依赖上的属性必须通过 attributes { } 这个 Action 形式设置，
    // 直接读写 dependency.attributes 拿到的是只读容器（ImmutableAttributes）。
    compileOnly(
        project(":common").apply {
            attributes { attribute(LoaderAttribute.KEY, LoaderAttribute.COMMON) }
        }
    )
    add("commonJava", project(":common").apply { targetConfiguration = "commonJava" })
    add("commonResources", project(":common").apply { targetConfiguration = "commonResources" })
}

// common 的 .java 直接参与本模块编译（而不是引用编译好的 class）
tasks.named<JavaCompile>("compileJava") {
    dependsOn(commonJava)
    source(commonJava)
}

// common 的资源（mixins.json / pack.mcmeta / assets …）合并进本模块资源
tasks.named<ProcessResources>("processResources") {
    dependsOn(commonResources)
    from(commonResources)
}

tasks.named<Javadoc>("javadoc") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava, commonResources)
    from(commonJava)
    from(commonResources)
}
