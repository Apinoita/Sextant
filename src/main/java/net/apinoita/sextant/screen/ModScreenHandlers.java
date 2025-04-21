package net.apinoita.sextant.screen;

import net.apinoita.sextant.Sextant;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
        public static final ScreenHandlerType<SextantScreenHandler> SEXTANT_SCREEN_HANDLER =
                Registry.register(Registries.SCREEN_HANDLER, new Identifier(Sextant.MOD_ID, "sextant"),
                        new ExtendedScreenHandlerType<>(SextantScreenHandler::new));

        public static void registerScreenHandlers() {
            Sextant.LOGGER.info("Registering Screen Handlers for " + Sextant.MOD_ID);
        }
    }
