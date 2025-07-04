package net.apinoita.sextant.util.config;

import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.apinoita.sextant.Sextant;
import net.apinoita.sextant.util.AngleUnits;
import net.minecraft.util.Identifier;

public class ModClientConfig extends Config {
    public ModClientConfig() {
        super(Identifier.of(Sextant.MOD_ID, "client"));
    }

    @NonSync
    public AngleUnits angleUnit = AngleUnits.DEGREES;
}
