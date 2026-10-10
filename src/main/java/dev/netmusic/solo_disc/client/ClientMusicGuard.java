package dev.netmusic.solo_disc.client;

import dev.netmusic.solo_disc.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.sound.PlayStreamingSourceEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class ClientMusicGuard {
    private static final Set<SoundInstance> RECORDS = Collections.newSetFromMap(new IdentityHashMap<>());
    private static boolean suppressing;

    private ClientMusicGuard() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientMusicGuard::onStreamingStarted);
        NeoForge.EVENT_BUS.addListener(ClientMusicGuard::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientMusicGuard::onLogout);
    }

    private static void onStreamingStarted(PlayStreamingSourceEvent event) {
        SoundInstance sound = event.getSound();
        if (sound instanceof NetworkRecord record && record.netmusic_solo_disc$isStreamReady()) {
            // Source events run on the audio thread; all tracker mutations stay on the client thread.
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.execute(() -> {
                if (minecraft.level != null && minecraft.getSoundManager().isActive(sound)) {
                    RECORDS.add(sound);
                }
            });
        }
    }

    private static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            RECORDS.clear();
            suppressing = false;
            return;
        }
        RECORDS.removeIf(sound -> !minecraft.getSoundManager().isActive(sound)
                || sound instanceof TickableSoundInstance tickable && tickable.isStopped());
        Vec3 listener = minecraft.gameRenderer.getMainCamera().getPosition();
        suppressing = ClientConfig.ENABLED.get()
                && minecraft.options.getSoundSourceVolume(SoundSource.MASTER) > 0
                && minecraft.options.getSoundSourceVolume(SoundSource.RECORDS) > 0
                && RECORDS.stream().anyMatch(sound -> isAudible(sound, listener));
    }

    public static boolean isAudible(SoundInstance sound, Vec3 listener) {
        if (sound.getVolume() <= 0 || !sound.canPlaySound()) {
            return false;
        }
        if (sound.isRelative() || sound.getAttenuation() == SoundInstance.Attenuation.NONE) {
            return true;
        }
        if (sound.getSound() == null) {
            return false;
        }
        double range = Math.max(sound.getVolume(), 1) * sound.getSound().getAttenuationDistance();
        return listener.distanceToSqr(sound.getX(), sound.getY(), sound.getZ()) < range * range;
    }

    public static boolean isBackground(SoundInstance sound) {
        return MusicGuardPolicy.isBackground(sound, ClientConfig.SUPPRESS_OTHER_MOD_MUSIC.get(), ClientConfig.EXTRA_BACKGROUND_SOUNDS.get());
    }

    public static boolean isSuppressing() {
        return suppressing;
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        RECORDS.clear();
        suppressing = false;
    }
}
