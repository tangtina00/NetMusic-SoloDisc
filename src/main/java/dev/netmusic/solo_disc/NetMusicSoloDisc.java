package dev.netmusic.solo_disc;

import dev.netmusic.solo_disc.client.ClientMusicGuard;
import dev.netmusic.solo_disc.config.ClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = NetMusicSoloDisc.MOD_ID, dist = Dist.CLIENT)
public class NetMusicSoloDisc {
    public static final String MOD_ID = "netmusic_solo_disc";

    public NetMusicSoloDisc(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "NetMusicSoloDisc-client.toml");
        ClientMusicGuard.register();
    }
}
