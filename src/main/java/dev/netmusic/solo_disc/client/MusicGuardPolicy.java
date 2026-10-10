package dev.netmusic.solo_disc.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public final class MusicGuardPolicy {
    private static final String CATACLYSM_BOSS_SOUND = "com.github.L_Ender.cataclysm.client.sound.BossMusicSound";

    private MusicGuardPolicy() {
    }

    public static boolean isBackground(SoundInstance sound, boolean suppressOtherModMusic, List<? extends String> extraSounds) {
        if (sound instanceof NetworkRecord) {
            return false;
        }
        return sound.getSource() == SoundSource.MUSIC
                || suppressOtherModMusic && sound.getClass().getName().equals(CATACLYSM_BOSS_SOUND)
                || extraSounds.contains(sound.getLocation().toString());
    }
}
