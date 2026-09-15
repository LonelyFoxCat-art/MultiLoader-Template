package com.example.examplemod.mixin;

import com.example.examplemod.Constants;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 只在 Fabric 端应用的 Mixin 示例
 * （由 fabric/src/main/resources/examplemod.mixins.json 的 client 段声明）。
 */
@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        Constants.LOG.info("This line is printed by an example mod mixin from Fabric!");
    }
}
