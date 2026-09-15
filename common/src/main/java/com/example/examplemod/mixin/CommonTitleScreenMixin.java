package com.example.examplemod.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.example.examplemod.Constants;

@Mixin(TitleScreen.class)
public class CommonTitleScreenMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private void examplemod$onInit(CallbackInfo ci) {
        Constants.LOG.info("这是来自 common 模块的通用 Mixin！两端都会执行这段代码。");
    }
}
