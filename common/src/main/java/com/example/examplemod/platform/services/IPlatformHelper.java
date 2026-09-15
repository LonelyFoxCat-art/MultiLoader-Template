package com.example.examplemod.platform.services;

/**
 * common 代码通过它询问“当前跑在哪个加载器上”。
 * 实现类分别位于 fabric / neoforge 模块，通过 META-INF/services 注册。
 */
public interface IPlatformHelper {

    /**
     * 获取当前平台名称。
     *
     * @return 当前平台名称。
     */
    String getPlatformName();

    /**
     * 检查指定 id 的模组是否已加载。
     *
     * @param modId 要检查的模组 id。
     * @return 已加载返回 true，否则返回 false。
     */
    boolean isModLoaded(String modId);

    /**
     * 检查当前是否处于开发环境。
     *
     * @return 开发环境返回 true，否则返回 false。
     */
    boolean isDevelopmentEnvironment();

    /**
     * 以字符串形式获取环境类型名称。
     *
     * @return 环境类型名称。
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
