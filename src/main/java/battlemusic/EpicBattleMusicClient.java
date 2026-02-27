package battlemusic;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class EpicBattleMusicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(MusicHandler::tick);
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> BossTextHud.tick());

        HudRenderCallback.EVENT.register(new BossTextHud());
    }
}