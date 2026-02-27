package battlemusic;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class BattleMusicInstance extends MovingSoundInstance {
    private final int maxFadeTime;
    
    private boolean isStopping = false; 
    private boolean shouldBeSilent = false; 

    public BattleMusicInstance(SoundEvent sound) {
        super(sound, SoundCategory.MASTER, Random.create());
        
        this.repeat = false; 
        
        this.repeatDelay = 0;
        this.volume = 0.01f; 
        this.maxFadeTime = ModConfig.fadeTime;
        this.relative = true; 
    }

    @Override
    public void tick() {
        if (this.isDone()) return;

        float targetVolume = ModConfig.musicVolume / 100.0f;

        if (targetVolume <= 0f) {
            this.volume = 0f;
            return;
        }

        float fadeSpeed = targetVolume / (float) maxFadeTime;

        if (this.isStopping || this.shouldBeSilent) {
            if (this.volume > 0.0f) {
                this.volume -= fadeSpeed;
                if (this.volume < 0.0f) this.volume = 0.0f;
            }
            
            if (this.isStopping && this.volume <= 0.001f) {
                this.setDone();
            }
        } else {
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