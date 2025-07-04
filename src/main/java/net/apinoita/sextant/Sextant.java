package net.apinoita.sextant;

import net.apinoita.sextant.item.ModItems;
import net.apinoita.FailedScreenTryAgainSometime.screen.ModScreenHandlers;
import net.apinoita.sextant.util.ModCustomTrades;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apinoita.sextant.util.config.Configs;

public class Sextant implements ModInitializer {
	public static final String MOD_ID = "sextant";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModCustomTrades.registerCustomTrades();
		ModScreenHandlers.registerScreenHandlers();
		Configs.init();
	}
}