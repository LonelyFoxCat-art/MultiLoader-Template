package com.example.examplemod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组的公共常量。
 *
 * <p>{@link #MOD_ID} 必须与 gradle.properties 里的 {@code mod_id} 保持一致，
 * 因为资源文件名（{@code <mod_id>.mixins.json}、{@code assets/<mod_id>/...}）
 * 以及 fabric.mod.json / neoforge.mods.toml 都由该属性展开生成。
 */
public class Constants {

    public static final String MOD_ID = "examplemod";
    public static final String MOD_NAME = "ExampleMod";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
}
