package com.example.examplemod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge 入口点，通过 {@code @Mod} 注解登记，
 * modId 必须与 neoforge.mods.toml 中的一致。
 */
@Mod(Constants.MOD_ID)
public class ExampleMod {

    /**
     * NeoForge 支持构造器注入：这里的 {@link IEventBus} 就是本模组的 mod 事件总线。
     */
    public ExampleMod(IEventBus eventBus) {

        // NeoForge 加载器准备好之后会构造这个类。
        // 这里可以同时访问 NeoForge 与 common 的代码。

        // 用 NeoForge 引导 common 模组。
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        // 需要注册 DeferredRegister 时，在这里挂到 mod 总线上：
        // ModItems.REGISTER.register(eventBus);
    }
}
