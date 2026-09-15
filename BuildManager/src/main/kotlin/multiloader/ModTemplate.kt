package multiloader

import org.gradle.api.Project

/** 读取根 gradle.properties 中的必填字段，缺失时立即报错，避免生成出错的模组元数据。 */
fun Project.modProperty(name: String): String =
    providers.gradleProperty(name)
        .orNull
        ?.trim()
        ?.takeUnless { it.isEmpty() }
        ?: error("gradle.properties 中缺少必填字段 '$name'（项目 ${project.path}）")

/** 读取根 gradle.properties 中允许为空的字段。 */
fun Project.optionalModProperty(name: String): String = providers.gradleProperty(name).getOrElse("")

/**
 * 注入到资源模板里的全部变量，这里是唯一登记处：
 * - TOML 场景：META-INF/mods.toml、META-INF/neoforge.mods.toml
 * - JSON 场景请改用 [jsonTemplateProperties]
 *
 * 在 gradle.properties 里新增字段后，记得同步加到这里。
 */
fun Project.templateProperties(): Map<String, Any> =
    linkedMapOf(
        // 项目
        "version" to version.toString(),
        "group" to group.toString(),
        "description" to (description ?: ""),
        "mod_id" to modProperty("mod_id"),
        "mod_name" to modProperty("mod_name"),
        "mod_author" to modProperty("mod_author"),
        "license" to modProperty("license"),
        "credits" to optionalModProperty("credits"),
        "java_version" to modProperty("java_version").toInt(),
        // Minecraft
        "minecraft_version" to modProperty("minecraft_version"),
        "minecraft_version_range" to modProperty("minecraft_version_range"),
        // Fabric
        "fabric_loader_version" to modProperty("fabric_loader_version"),
        "fabric_api_version" to modProperty("fabric_api_version"),
        "fabric_minecraft_dependency" to modProperty("fabric_minecraft_dependency"),
        // NeoForge
        "neoforge_version" to modProperty("neoforge_version"),
        "neoforge_version_range" to modProperty("neoforge_version_range"),
        "neoforge_loader_version_range" to modProperty("neoforge_loader_version_range"),
    )

/**
 * JSON 场景使用的变量表：对字符串做 JSON 转义。
 * gradle.properties 里的 `\n` 会被解析成真正的换行，直接写进 JSON 会破坏文件格式。
 */
fun Project.jsonTemplateProperties(): Map<String, Any> =
    templateProperties().mapValues { (_, value) -> if (value is String) value.escapeForJson() else value }

private fun String.escapeForJson(): String =
    buildString(this@escapeForJson.length) {
        for (char in this@escapeForJson) {
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                else ->
                    if (char < ' ') {
                        append("\\u%04x".format(char.code))
                    } else {
                        append(char)
                    }
            }
        }
    }
