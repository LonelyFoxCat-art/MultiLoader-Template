import multiloader.LoaderAttribute
import multiloader.modProperty
import multiloader.tagLoaderVariants

// ==========================================================================================
//  multiloader.neoforge —— neoforge 模块专用（multiloader.loader + ModDevGradle）
//  · client / server / data(数据生成) 三个运行配置
//  · src/generated/resources 作为 datagen 输出目录并回灌为资源目录
// ==========================================================================================

plugins {
    `java-library`
    id("multiloader.loader")
    id("net.neoforged.moddev")
}

val modId: String = modProperty("mod_id")
val projectPath: String = path

neoForge {
    version = modProperty("neoforge_version")

    // common 里放了 AccessTransformer 时自动启用
    val accessTransformer = rootProject.file("common/src/main/resources/META-INF/accesstransformer.cfg")
    if (accessTransformer.exists()) {
        accessTransformers.from(accessTransformer.absolutePath)
    }

    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            // 与 Fabric 的运行配置命名风格保持统一
            ideName.set("NeoForge ${name.replaceFirstChar(Char::titlecase)} ($projectPath)")
        }

        create("client") {
            client()
            gameDirectory.set(layout.projectDirectory.dir("runs/client"))
        }

        // 数据生成：./gradlew :neoforge:runData
        create("data") {
            clientData()
            gameDirectory.set(layout.projectDirectory.dir("runs/data"))
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources").absolutePath,
                "--existing", file("src/main/resources").absolutePath,
            )
        }

        create("server") {
            server()
            gameDirectory.set(layout.projectDirectory.dir("runs/server"))
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.named("main").get())
        }
    }
}

// datagen 的输出目录同时也是资源目录
sourceSets.named("main") {
    resources.srcDir("src/generated/resources")
}

tagLoaderVariants(LoaderAttribute.NEOFORGE, "jarJar")
