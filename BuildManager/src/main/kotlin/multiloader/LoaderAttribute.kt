package multiloader

import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.plugins.JavaPluginExtension

/**
 * common 模块的源码与资源要被 fabric / neoforge 两个模块合并进各自的最终 jar，
 * 而 Loom 与 ModDevGradle 又会往变体上挂各自的属性，Gradle 无法自动决定该选哪一个变体。
 *
 * 这里用一个自定义属性显式标注“这个变体属于哪个加载器”，
 * 消费方在依赖上声明 `loader = common` 即可精确匹配 common 暴露出来的变体。
 */
object LoaderAttribute {
    const val NAME: String = "io.github.multiloader.loader"

    val KEY: Attribute<String> = Attribute.of(NAME, String::class.java)

    const val COMMON: String = "common"
    const val FABRIC: String = "fabric"
    const val FORGE: String = "forge"
    const val NEOFORGE: String = "neoforge"
}

/**
 * 给当前项目的“出/入”变体打上加载器标签。
 *
 * @param loader 取值见 [LoaderAttribute] 中的常量
 * @param extra  加载器插件额外创建、同样需要标注的配置名
 *               （Fabric: includeInternal / modCompileClasspath；NeoForge: jarJar）
 */
fun Project.tagLoaderVariants(loader: String, vararg extra: String) {
    val taggedNames =
        setOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements") + extra

    configurations.configureEach {
        if (name in taggedNames) {
            attributes.attribute(LoaderAttribute.KEY, loader)
        }
    }

    extensions.findByType(JavaPluginExtension::class.java)?.sourceSets?.configureEach {
        val classpathNames = setOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName)
        configurations.configureEach {
            if (name in classpathNames) {
                attributes.attribute(LoaderAttribute.KEY, loader)
            }
        }
    }
}
