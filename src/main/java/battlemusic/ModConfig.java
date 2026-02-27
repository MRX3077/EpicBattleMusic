package battlemusic;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
    
    @Entry(category = "music", isSlider = true, min = 0, max = 100)
    public static int musicVolume = 100;
    
    @Entry(category = "music", min = 10, max = 200)
    public static int detectionRange = 80;
    
    @Entry(category = "music", min = 20, max = 5000)
    public static int bossHealthThreshold = 200; 
    
    @Entry(category = "music", min = 20, max = 200)
    public static int fadeTime = 80;
    
    @Entry(category = "music")
    public static boolean enableAutoMusic = true;

    @Entry(category = "text")
    public static boolean enableBossText = true;
}