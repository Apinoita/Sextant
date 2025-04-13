package net.apinoita.sextant.util.config;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public class Configs {
    public static ModClientConfig clientConfig = ConfigApiJava.registerAndLoadConfig(ModClientConfig::new, RegisterType.CLIENT);

    public static void init() {}
}
