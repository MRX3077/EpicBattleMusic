package battlemusic;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter; // <-- NOVO IMPORT DA 1.21
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class BossTextHud implements HudRenderCallback {
    
    private static String currentKey = "";
    private static int displayTicks = 0;
    private static int totalDuration = 0;
    private static int fadeInTicks = 0;
    private static int fadeOutTicks = 0;
    private static boolean isActive = false;
    private static final Random random = Random.create();

    private static final int COLOR_RED = 0xFF0000; 
    private static final float SHAKE_INTENSITY = 2.0f; 

    public static void show(String key, int duration, int fadeIn, int fadeOut) {
        currentKey = key;
        totalDuration = duration;
        displayTicks = duration;
        fadeInTicks = fadeIn;
        fadeOutTicks = fadeOut;
        isActive = true;
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        if (!isActive || displayTicks <= 0) {
            isActive = false;
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        TextRenderer textRenderer = client.textRenderer;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        Text textComponent = Text.translatable(currentKey);

        float alpha = 1.0f;
        int timeElapsed = totalDuration - displayTicks;
        
        if (timeElapsed < fadeInTicks) {
            alpha = (float) timeElapsed / (float) fadeInTicks;
        } else if (displayTicks < fadeOutTicks) {
            alpha = (float) displayTicks / (float) fadeOutTicks;
        }
        
        alpha = MathHelper.clamp(alpha, 0.0f, 1.0f);
        
        if (alpha <= 0.01f) return;

        int alphaInt = (int) (alpha * 255);
        int colorWithAlpha = (alphaInt << 24) | (COLOR_RED);

        float currentShake = SHAKE_INTENSITY * alpha; 
        float shakeX = (random.nextFloat() - 0.5f) * currentShake;
        float shakeY = (random.nextFloat() - 0.5f) * currentShake;

        int textWidth = textRenderer.getWidth(textComponent);
        int x = (width - textWidth) / 2;
        int y = height - 65; 

        context.getMatrices().push();
        context.getMatrices().translate(shakeX, shakeY, 0);
        
        context.drawTextWithShadow(textRenderer, textComponent, x, y, colorWithAlpha);
        
        context.getMatrices().pop();
    }

    public static void tick() {
        if (displayTicks > 0) {
            displayTicks--;
        }
    }
}