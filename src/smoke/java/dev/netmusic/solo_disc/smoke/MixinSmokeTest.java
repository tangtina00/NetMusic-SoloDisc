package dev.netmusic.solo_disc.smoke;

import com.mojang.logging.LogUtils;
import dev.netmusic.solo_disc.NetMusicSoloDisc;
import dev.netmusic.solo_disc.client.NetworkRecord;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(modid = NetMusicSoloDisc.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MixinSmokeTest {
    private MixinSmokeTest() {
    }

    @SubscribeEvent
    public static void checkTarget(FMLClientSetupEvent event) {
        try {
            Class<?> target = Class.forName("com.github.tartaricacid.netmusic.compat.sbackpack.NetMusicBackpackSound");
            if (!NetworkRecord.class.isAssignableFrom(target)) {
                throw new IllegalStateException("NetMusic backpack mixin did not apply");
            }
            LogUtils.getLogger().info("NetMusic:Solo Disc smoke check: NetMusic backpack mixin applied");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("NetMusic is missing from the smoke test", e);
        }
    }
}
