package net.apinoita.sextant.item;

import net.apinoita.sextant.Sextant;
import net.apinoita.FailedScreenTryAgainSometime.item.custom.ModTestItem;
import net.apinoita.sextant.item.custom.InventoryTestItem;
import net.apinoita.sextant.item.custom.SextantItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item SEXTANT = registerItem("sextant", new SextantItem(new FabricItemSettings().maxCount(1)));
    public static final Item TEST = registerItem("test", new InventoryTestItem(new FabricItemSettings().maxCount(1)));

    private static void addItemsToToolTabItemGroup(FabricItemGroupEntries entries){
        entries.add(SEXTANT);
    }

    public static void registerModItems(){
        Sextant.LOGGER.info("Registerng items for " + Sextant.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToToolTabItemGroup);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(Sextant.MOD_ID, name), item);
    }
}
