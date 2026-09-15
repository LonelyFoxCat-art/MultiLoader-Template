package com.example.examplemod.platform;

import com.example.examplemod.Constants;
import com.example.examplemod.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/**
 * ServiceLoader 是 Java 自带的能力，用来定位“同一接口在不同环境下的不同实现”。
 * 在 MultiLoader 场景里，我们用它把 common 代码中的抽象 API，在运行时替换成加载器专属实现。
 */
public class Services {

    /**
     * 这里提供一个 platform helper，用于获知模组运行在哪个平台上。
     * 例如可以用它判断当前是 NeoForge 还是 Fabric，或者询问加载器某个模组是否已加载。
     */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * 加载当前环境对应的服务实现。
     *
     * <p>实现方需要手动登记：在 META-INF/services 目录下，以服务接口的全限定名作为文件名建立文本文件，
     * 文件内容写上该平台要加载的实现类全限定名。
     * 本模板中 fabric 模块指向 FabricPlatformHelper，neoforge 模块指向 NeoForgePlatformHelper。
     */
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
