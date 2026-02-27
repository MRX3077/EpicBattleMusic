package battlemusic;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class BossTextHud implements HudRenderCallback {
    
    private static String currentKey = "";
    private static int displayTicks = 0;      // Contador regressivo
    private static int totalDuration = 0;     // Duração total inicial
    private static int fadeInTicks = 0;       // Tempo de entrada
    private static int fadeOutTicks = 0;      // Tempo de saída
    private static boolean isActive = false;
    private static final Random random = Random.create();

    private static final int COLOR_RED = 0xFF0000; 
    private static final float SHAKE_INTENSITY = 2.0f; 

    /**
     * @param key Chave de tradução (ex: "battlemusic.phrase.1")
     * @param duration Duração total em ticks (ex: 300 para 15s)
     * @param fadeIn Tempo para aparecer totalmente (ex: 40 ticks)
     * @param fadeOut Tempo para desaparecer no final (ex: 40 ticks)
     */
    public static void show(String key, int duration, int fadeIn, int fadeOut) {
        currentKey = key;
        totalDuration = duration;
        displayTicks = duration;
        fadeInTicks = fadeIn;
        fadeOutTicks = fadeOut;
        isActive = true;
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (!isActive || displayTicks <= 0) {
            isActive = false;
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        TextRenderer textRenderer = client.textRenderer;
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Traduz a chave para o texto real no idioma do jogador
        Text textComponent = Text.translatable(currentKey);

        // --- CÁLCULO DE OPACIDADE (ALPHA) ---
        float alpha = 1.0f;
        int timeElapsed = totalDuration - displayTicks; // Tempo que já passou
        
        if (timeElapsed < fadeInTicks) {
            // FADE IN: Aumenta de 0 a 1
            alpha = (float) timeElapsed / (float) fadeInTicks;
        } else if (displayTicks < fadeOutTicks) {
            // FADE OUT: Diminui de 1 a 0
            alpha = (float) displayTicks / (float) fadeOutTicks;
        }
        
        // Garante que fique entre 0 e 1
        alpha = MathHelper.clamp(alpha, 0.0f, 1.0f);
        
        // Se alpha for muito baixo, nem desenha para economizar processamento
        if (alpha <= 0.01f) return;

        // Converte alpha para formato ARGB
        int alphaInt = (int) (alpha * 255);
        // Combina o Alpha com o Vermelho (00FF0000)
        int colorWithAlpha = (alphaInt << 24) | (COLOR_RED);

        // --- TREMEDEIRA ---
        // A tremedeira diminui um pouco se estiver no fade out para ficar mais suave
        float currentShake = SHAKE_INTENSITY * alpha; 
        float shakeX = (random.nextFloat() - 0.5f) * currentShake;
        float shakeY = (random.nextFloat() - 0.5f) * currentShake;

        // Posição: Centralizado horizontalmente, um pouco acima da hotbar
        int textWidth = textRenderer.getWidth(textComponent);
        int x = (width - textWidth) / 2;
        int y = height - 65; 

        // Renderização com Matrizes para o Shake
        context.getMatrices().push();
        context.getMatrices().translate(shakeX, shakeY, 0);
        
        // Desenha o texto com sombra
        context.drawTextWithShadow(textRenderer, textComponent, x, y, colorWithAlpha);
        
        context.getMatrices().pop();
    }

    public static void tick() {
        if (displayTicks > 0) {
            displayTicks--;
        }
    }
}