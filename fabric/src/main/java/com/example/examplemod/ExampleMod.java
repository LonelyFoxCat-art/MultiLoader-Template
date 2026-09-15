package com.example.examplemod;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric 入口点，在 fabric.mod.json 的 {@code entrypoints.main} 中登记。
 */
public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {

        // Fabric 加载器准备好之后会调用这个方法。
        // 这里可以同时访问 Fabric 与 common 的代码。

        // 用 Fabric 引导 common 模组。
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
}
