package net.apinoita.sextant.util.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.apinoita.sextant.Sextant;
import net.apinoita.sextant.util.AngleUnits;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;

/*public class ModConfig {

    public static AngleUnits currentAngleUnit;
    public static boolean radians;

    public static Screen createConfigScreen() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(Text.translatable("config.title.sextant"));

        builder.setSavingRunnable(() -> {
            // Serialise the config into the config file. This will be called last after all variables are updated.
        });

        ConfigCategory client = builder.getOrCreateCategory(Text.translatable("config.category.sextant.client"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        client.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.option.sextant.angle_unit.name"), radians)
                .setDefaultValue(false) // Recommended: Used when user click "Reset"
                .setTooltip(Text.translatable("config.option.sextant.angle_unit.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> radians = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        client.addEntry(entryBuilder.startEnumSelector(Text.translatable("config.option.sextant.angle_unit.name"), AngleUnits.class, currentAngleUnit)
                .setDefaultValue(AngleUnits.DEGREES) // Recommended: Used when user click "Reset"
                .setTooltip(Text.translatable("config.option.sextant.angle_unit.tooltip")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> currentAngleUnit = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        return builder.build();
    }
}*/
@Config(name = "sextant")
public class ModConfig implements ConfigData {
    boolean toggleA = true;
}
