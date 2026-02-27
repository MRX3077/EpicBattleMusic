package battlemusic;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicBattleMusic implements ModInitializer {
    public static final String MOD_ID = "battlemusic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, ModConfig.class);
        ModSounds.registerSounds();
        LOGGER.info("Epic Battle Music carregado!");
    }
}