# MultiLoader-Template · Minecraft（Kotlin DSL）

一个 **Minecraft 26.1** 的多加载器（Fabric + NeoForge）模组开发模板。
所有构建脚本使用 **Gradle Kotlin DSL（`.gradle.kts`）**，共享构建逻辑集中在 **`BuildManager`** 这个
included build 里，`common` / `fabric` / `neoforge` 三个模块的 `build.gradle.kts` 因此可以薄到只有一行插件声明。

模板参考了 [jaredlll08/MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template)（`26.1` / `26.2` 分支），

## 目录结构

```
MultiLoader-Template/
├── BuildManager/                      # ★ 多平台构建逻辑（included build，Kotlin DSL 预编译脚本插件）
│   ├── build.gradle.kts               #   kotlin-dsl + 把 Loom / ModDevGradle 放到 classpath
│   ├── settings.gradle.kts
│   ├── gradle.properties              #   loom_version / moddev_version
│   └── src/main/kotlin/
│       ├── multiloader.base.gradle.kts      # 所有模块共享：工具链、仓库、jar、资源展开、发布
│       ├── multiloader.common.gradle.kts    # common：NeoForm 纯净环境 + 暴露源码/资源
│       ├── multiloader.loader.gradle.kts    # 加载器模块共享：把 common 源码/资源合并进来
│       ├── multiloader.fabric.gradle.kts    # fabric：Loom + 运行配置
│       ├── multiloader.neoforge.gradle.kts  # neoforge：ModDevGradle + client/data/server 运行配置
│       ├── multiloader.root.gradle.kts      # 根项目：buildAllMods / printEnvironment
│       └── multiloader/
│           ├── LoaderAttribute.kt           # 变体属性（解决 common 变体歧义）
│           └── ModTemplate.kt               # gradle.properties → 资源模板变量（唯一登记处）
├── common/                            # 跨加载器代码（只能用原版 API + Mixin）
│   ├── build.gradle.kts               #   plugins { id("multiloader.common") }
│   └── src/main/
│       ├── java/com/example/examplemod/{CommonClass,Constants}.java
│       │                        /mixin/MixinMinecraft.java
│       │                        /platform/Services.java
│       │                        /platform/services/IPlatformHelper.java
│       └── resources/{examplemod.mixins.json, pack.mcmeta, assets/examplemod/icon.png}
├── fabric/                            # Fabric 端
│   ├── build.gradle.kts               #   plugins { id("multiloader.fabric") }
│   └── src/main/
│       ├── java/.../{ExampleMod.java, mixin/MixinTitleScreen.java, platform/FabricPlatformHelper.java}
│       └── resources/{fabric.mod.json, examplemod.fabric.mixins.json, META-INF/services/...}
├── neoforge/                          # NeoForge 端
│   ├── build.gradle.kts               #   plugins { id("multiloader.neoforge") }
│   └── src/main/
│       ├── java/.../{ExampleMod.java, mixin/MixinTitleScreen.java, platform/NeoForgePlatformHelper.java}
│       └── resources/{META-INF/neoforge.mods.toml, examplemod.neoforge.mixins.json, META-INF/services/...}
├── .github/workflows/build.yml        # CI：JDK 25 + buildAllMods + 上传两端 jar
├── docs/                              # BuildManager 详解、版本升级指南
├── build.gradle.kts / settings.gradle.kts / gradle.properties
└── gradlew / gradlew.bat / gradle/wrapper/…   # Gradle 9.7.1
```

`common` 的源码与资源会被 **合并** 进 `fabric` / `neoforge` 的产物：
两个 loader jar 里都能看到 `CommonClass.class`、`Constants.class`、`Services.class`、
`examplemod.fabric.mixins.json`、`pack.mcmeta`、`assets/examplemod/icon.png`。

## 快速开始

前置条件：**JDK 25**（没有也没关系，`settings.gradle.kts` 里的 foojay resolver 会自动下载）。
不需要本地安装 Gradle，用 wrapper 即可。

## BuildManager（多平台支持）

`BuildManager` 是一个 **included build**（`settings.gradle.kts` 里 `pluginManagement { includeBuild("BuildManager") }`），
里面的每个 `*.gradle.kts` 都是一个 **预编译脚本插件**，文件名即插件 id：


| 插件 id                | 应用位置             | 职责                                                                                                        |
| ---------------------- | -------------------- | ----------------------------------------------------------------------------------------------------------- |
| `multiloader.root`     | 根项目               | `buildAllMods`、`printEnvironment`                                                                          |
| `multiloader.base`     | 由其它插件继承       | Java 工具链、UTF-8、仓库、jar/LICENSE/manifest、资源模板展开、maven-publish                                 |
| `multiloader.common`   | `common`             | ModDevGradle**vanilla(NeoForm)** 模式、Mixin/MixinExtras `compileOnly`、暴露 `commonJava`/`commonResources` |
| `multiloader.loader`   | `fabric`、`neoforge` | 消费`common` 的源码与资源并合并进本模块产物                                                                 |
| `multiloader.fabric`   | `fabric`             | Loom、Minecraft/Loader/Fabric API 依赖、`runClient`/`runServer`                                             |
| `multiloader.neoforge` | `neoforge`           | ModDevGradle、`runClient`/`runData`/`runServer`、`src/generated/resources`                                  |

## License

模板代码为 **CC0-1.0**（见 `LICENSE`），可自由使用、修改、商用，无需署名。
`gradle.properties` 里的 `license` 字段是你**模组**的许可证，与模板本身的许可无关，请按需修改。
