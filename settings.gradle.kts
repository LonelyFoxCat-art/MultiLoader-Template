val NEO_FORGE_MIRRORS = mapOf(
    "maven.neoforged.net/releases"    to "https://neoforged.forgecdn.net/releases",
    "maven.neoforged.net/mojang-meta" to "https://neoforged.forgecdn.net/mojang-meta",
)

gradle.beforeProject {
    repositories.all {
        if (this is MavenArtifactRepository) {
            val urlStr = url.toString()
            val normalizedUrl = urlStr.trimEnd('/')
            NEO_FORGE_MIRRORS.entries.firstOrNull { normalizedUrl.endsWith(it.key.trimEnd('/')) } ?.value ?.let { mirrorUrl ->
                if (urlStr != mirrorUrl) {
                    url = uri(mirrorUrl)
                    logger.lifecycle("⚠️ Redirected [$name]: $urlStr -> $mirrorUrl")
                }
            }
        }
    }
}

pluginManagement {
    includeBuild("BuildManager")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://neoforged.forgecdn.net/releases")
        maven("https://neoforged.forgecdn.net/mojang-meta")
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "MultiLoader-Template"
include("common", "fabric", "neoforge")
