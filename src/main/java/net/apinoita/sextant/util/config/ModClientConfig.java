package net.apinoita.sextant.util.config;

import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.apinoita.sextant.Sextant;
import net.apinoita.sextant.util.AngleUnits;
import net.minecraft.util.Identifier;

public class ModClientConfig extends Config {
    public ModClientConfig() {
        super(Identifier.of(Sextant.MOD_ID, "config.client"));
    }

    @NonSync
    public AngleUnits angleUnit = AngleUnits.DEGREES;
    @NonSync
    @ValidatedFloat.Restrict(min = 0f, max = 45f)
    public float snappingThreshold = 3f;
    @NonSync
    @ValidatedFloat.Restrict(min = 0f, max = 45f)
    public float snappingThresholdSpyglass = 1f;
}
