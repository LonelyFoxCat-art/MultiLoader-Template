package com.example.examplemod;

import com.example.examplemod.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

/**
 * 这个类位于 common 模块，会在所有受支持的加载器之间共享。
 *
 * <p>写在这里的代码只能引用原版代码、原版自带的库，以及少数在各加载器上二进制兼容的第三方库。
 * 也就是说 common 代码不能直接使用加载器专有的概念（例如 NeoForge 事件、Fabric API 事件），
 * 但它能在所有受支持的加载器上运行。
 */
public class CommonClass {

    /**
     * 加载器专属模块可以引用并使用 common 模块里的任何代码。
     * 这样你就能把绝大部分逻辑写在这里，再由各加载器的入口点调用。
     */
    public static void init() {

        Constants.LOG.info(
                "Hello from Common init on {}! we are currently in a {} environment!",
                Services.PLATFORM.getPlatformName(),
                Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        // 各加载器通常都提供类似的能力，但没法在 common 代码里直接调用。
        // 常见的绕法是使用 Java 自带的 ServiceLoader 建立一层自己的抽象：
        // 在 common 里定义接口，在各加载器模块里给出实现。详见 Services 类。
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to {}", Constants.MOD_ID);
        }
    }
}
