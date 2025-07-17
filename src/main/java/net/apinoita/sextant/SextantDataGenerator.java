package net.apinoita.sextant;

import net.apinoita.sextant.datagen.ModTagProvider;
import net.apinoita.sextant.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SextantDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModTagProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}
