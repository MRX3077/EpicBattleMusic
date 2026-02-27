package battlemusic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import java.util.UUID;

public class MusicHandler {
    private static BattleMusicInstance currentMusic;
    private static LivingEntity currentBoss;
    private static UUID currentBossUUID;
    private static SoundEvent lastPlayedMusic; 
    private static int cooldown = 0;

    // --- NOVO: Variável para controlar o delay do texto ---
    private static int textDelayTimer = -1; 

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null || !ModConfig.enableAutoMusic) {
            forceStop(client);
            return;
        }

        // Lógica do Texto
        if (textDelayTimer > 0) {
            textDelayTimer--;
            // Quando chega em 0, dispara o texto
            if (textDelayTimer == 0 && ModConfig.enableBossText) {
                String phraseKey = BossPhrases.getRandomPhraseKey();
                
                // CONFIGURAÇÃO DE TEMPO:
                // Duração Total: 300 ticks (15 segundos)
                // Fade In: 40 ticks (2 segundos)
                // Fade Out: 60 ticks (3 segundos)
                BossTextHud.show(phraseKey, 200, 40, 60);
            }
        }

        // --- LÓGICA DA PLAYLIST (Mantida igual) ---
        if (currentMusic != null) {
            boolean isEnginePlaying = client.getSoundManager().isPlaying(currentMusic);
            if (!isEnginePlaying) {
                currentMusic = null; 
            }
        }

        if (cooldown > 0) {
            cooldown--;
            if (currentMusic != null) {
                client.getMusicTracker().stop();
            }
            return;
        }
        cooldown = 10;

        // --- CENÁRIO 1: JÁ ESTAMOS RASTREANDO UM BOSS ---
        if (currentBossUUID != null) {
            if (currentBoss == null || currentBoss.isRemoved()) {
                currentBoss = findBossByUUID(client, currentBossUUID);
            }

            if (currentBoss == null) {
                if (currentMusic != null) currentMusic.setSilent(true);
                return;
            }

            if (!currentBoss.isAlive() || currentBoss.getHealth() <= 0) {
                killMusic(); 
                return;
            }

            if (currentMusic == null) {
                EpicBattleMusic.LOGGER.info("Musica acabou. Iniciando proxima da playlist...");
                startNewMusic(client, currentBoss, lastPlayedMusic);
                return;
            }

            float dist = client.player.distanceTo(currentBoss);
            float limit = ModConfig.detectionRange;

            if (dist <= limit) {
                currentMusic.setSilent(false);
            } else {
                currentMusic.setSilent(true);
            }
            return;
        }

        // --- CENÁRIO 2: PROCURANDO NOVO BOSS ---
        Box box = new Box(client.player.getPos(), client.player.getPos()).expand(ModConfig.detectionRange);
        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, box, e -> e instanceof HostileEntity)) {
            if (entity.getMaxHealth() >= ModConfig.bossHealthThreshold && entity.isAlive()) {
                lastPlayedMusic = null; 
                startNewMusic(client, entity, null);
                break;
            }
        }
    }

    private static LivingEntity findBossByUUID(MinecraftClient client, UUID uuid) {
        for (Entity entity : client.world.getEntities()) {
            if (entity.getUuid().equals(uuid) && entity instanceof LivingEntity) {
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    private static void startNewMusic(MinecraftClient client, LivingEntity boss, SoundEvent exclude) {
        if (!boss.isAlive()) {
            killMusic();
            return;
        }

        SoundEvent nextMusic;
        if (exclude != null) {
            nextMusic = ModSounds.getRandomMusicExcluding(exclude);
        } else {
            nextMusic = ModSounds.getRandomMusic();
        }

        if (nextMusic == null) return;

        currentBoss = boss;
        currentBossUUID = boss.getUuid();
        lastPlayedMusic = nextMusic; 
        
        currentMusic = new BattleMusicInstance(nextMusic);
        
        if (client.player.distanceTo(boss) > ModConfig.detectionRange) {
            currentMusic.setSilent(true);
        }

        client.getMusicTracker().stop();
        client.getSoundManager().play(currentMusic);
        EpicBattleMusic.LOGGER.info("Tocando musica: " + nextMusic.getId().toString());
        
        cooldown = 10; 

        // --- NOVO: Configura o delay do texto ---
        // Só dispara o texto se for o INÍCIO da batalha (exclude == null significa que é o primeiro boss encontrado)
        // Se for troca de música na playlist (exclude != null), talvez você não queira mostrar o texto de novo.
        // Se quiser mostrar sempre que troca a música, remova o "if (exclude == null)"
        if (exclude == null) {
            // Define delay entre 40 e 60 ticks (2 a 3 segundos)
            textDelayTimer = 40 + client.world.random.nextInt(21);
        }
    }

    private static void killMusic() {
        if (currentMusic != null) {
            currentMusic.setFinished(); 
        }
        currentBoss = null;
        currentBossUUID = null;
        lastPlayedMusic = null;
        textDelayTimer = -1; // Cancela qualquer texto pendente se o boss morrer
        EpicBattleMusic.LOGGER.info("Boss morto. Resetando sistema.");
    }

    private static void forceStop(MinecraftClient client) {
        if (currentMusic != null) {
            client.getSoundManager().stop(currentMusic);
            currentMusic = null;
        }
        currentBoss = null;
        currentBossUUID = null;
        lastPlayedMusic = null;
        textDelayTimer = -1;
    }
}