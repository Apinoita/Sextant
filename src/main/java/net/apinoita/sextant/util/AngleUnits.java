package net.apinoita.sextant.util;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import org.jetbrains.annotations.NotNull;

public enum AngleUnits implements EnumTranslatable {
    DEGREES,
    RADIANS;

    @NotNull
    @Override
    public String prefix() {
        return "sextant.AngleUnits";
    }
}