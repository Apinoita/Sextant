package net.apinoita.sextant.sound;

import net.apinoita.sextant.Sextant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent SEXTANT_START_USING = registerSoundEvent("sextant_start_using");
    public static final SoundEvent SEXTANT_STOP_USING = registerSoundEvent("sextant_stop_using");
    public static final SoundEvent SEXTANT_INSERT_ITEM = registerSoundEvent("sextant_insert_item");


    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(Sextant.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSounds(){
        Sextant.LOGGER.info("Registerng sounds for " + Sextant.MOD_ID);
    }
}
