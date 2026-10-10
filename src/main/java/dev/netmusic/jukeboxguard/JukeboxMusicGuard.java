package dev.netmusic.jukeboxguard;

import dev.netmusic.jukeboxguard.client.ClientMusicGuard;
import dev.netmusic.jukeboxguard.config.ClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = JukeboxMusicGuard.MOD_ID, dist = Dist.CLIENT)
public class JukeboxMusicGuard {
    public static final String MOD_ID = "jukeboxmusicguard";

    public JukeboxMusicGuard(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ClientMusicGuard.register();
    }
}
