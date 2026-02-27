package battlemusic;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class BattleMusicInstance extends MovingSoundInstance {
    private final int maxFadeTime;
    
    // Estados
    private boolean isStopping = false; 
    private boolean shouldBeSilent = false; 

    public BattleMusicInstance(SoundEvent sound) {
        super(sound, SoundCategory.MASTER, Random.create());
        
        // IMPORTANTE: repeat = false para permitir que a música acabe e troque
        this.repeat = false; 
        
        this.repeatDelay = 0;
        this.volume = 0.01f; 
        this.maxFadeTime = ModConfig.fadeTime;
        this.relative = true; 
    }

    @Override
    public void tick() {
        // Se o motor de som já finalizou a música, paramos.
        if (this.isDone()) return;

        // Pega o volume da sua config (0 a 100) e converte para 0.0 a 1.0
        float targetVolume = ModConfig.musicVolume / 100.0f;

        if (targetVolume <= 0f) {
            this.volume = 0f;
            return;
        }

        float fadeSpeed = targetVolume / (float) maxFadeTime;

        // --- LÓGICA DE FADE ---
        if (this.isStopping || this.shouldBeSilent) {
            // FADE OUT
            if (this.volume > 0.0f) {
                this.volume -= fadeSpeed;
                if (this.volume < 0.0f) this.volume = 0.0f;
            }
            
            // Se for para PARAR DE VEZ (Boss morreu) e o volume zerou, mata o som
            if (this.isStopping && this.volume <= 0.001f) {
                this.setDone();
            }
        } else {
            // FADE IN
            if (this.volume < targetVolume) {
                this.volume += fadeSpeed;
                if (this.volume > targetVolume) this.volume = targetVolume;
            } else if (this.volume > targetVolume) {
                this.volume = targetVolume;
            }
        }
    }

    public void setFinished() {
        this.isStopping = true;
    }

    public void setSilent(boolean silent) {
        this.shouldBeSilent = silent;
    }
}