package battlemusic;

import net.minecraft.util.math.random.Random;

public class BossPhrases {
    private static final Random RANDOM = Random.create();
    private static final int TOTAL_PHRASES = 300; 

    public static String getRandomPhraseKey() {
        int id = 1 + RANDOM.nextInt(TOTAL_PHRASES);
        return "battlemusic.phrase." + id;
    }
}