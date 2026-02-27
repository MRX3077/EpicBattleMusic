package battlemusic;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModSounds {
    public static final List<SoundEvent> MUSIC_LIST = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private static final int AMOUNT = 100; 

    public static void registerSounds() {
        for (int i = 1; i <= AMOUNT; i++) {
            Identifier id = new Identifier("battlemusic", "boss_theme_" + i);
            SoundEvent event = SoundEvent.of(id);
            Registry.register(Registries.SOUND_EVENT, id, event);
            MUSIC_LIST.add(event);
        }
        EpicBattleMusic.LOGGER.info("Registradas " + AMOUNT + " musicas.");
    }

    public static SoundEvent getRandomMusic() {
        if (MUSIC_LIST.isEmpty()) return null;
        return MUSIC_LIST.get(RANDOM.nextInt(MUSIC_LIST.size()));
    }

    public static SoundEvent getRandomMusicExcluding(SoundEvent exclude) {
        if (MUSIC_LIST.isEmpty()) return null;
        if (MUSIC_LIST.size() == 1) return MUSIC_LIST.get(0);

        SoundEvent next;
        do {
            next = MUSIC_LIST.get(RANDOM.nextInt(MUSIC_LIST.size()));
        } while (next == exclude);
        
        return next;
    }
}