package dev.netmusic.solo_disc.client;

import com.github.L_Ender.cataclysm.client.sound.BossMusicSound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class MusicGuardPolicyTest {
    @Test
    void musicCategoryIsBackground() {
        assertTrue(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.MUSIC, "minecraft:music.game"), false, List.of()));
    }

    @Test
    void recordsAndEffectsAreNotBackgroundByDefault() {
        assertFalse(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.RECORDS, "minecraft:music_disc.cat"), true, List.of()));
        assertFalse(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.HOSTILE, "minecraft:entity.zombie.ambient"), true, List.of()));
        assertFalse(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.HOSTILE, "cataclysm:leviathan_idle"), true, List.of()));
    }

    @Test
    void explicitlyConfiguredBackgroundSoundMatchesItsExactEventId() {
        List<String> ids = List.of("othermod:boss_theme");

        assertTrue(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.RECORDS, "othermod:boss_theme"), false, ids));
        assertFalse(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.RECORDS, "othermod:boss_theme_ambient"), false, ids));
        assertFalse(MusicGuardPolicy.isBackground(
                soundInCategory(SoundSource.RECORDS, "anothermod:boss_theme"), false, ids));
    }

    @Test
    void networkRecordNeverSuppressesItselfEvenIfItsCategoryAndIdMatch() {
        SoundInstance record = mock(SoundInstance.class, withSettings().extraInterfaces(NetworkRecord.class));
        when(record.getSource()).thenReturn(SoundSource.MUSIC);
        when(record.getLocation()).thenReturn(ResourceLocation.parse("netmusic:record"));

        assertFalse(MusicGuardPolicy.isBackground(record, true, List.of("netmusic:record")));
    }

    @Test
    void recognizedModMusicIsBackgroundWhenOtherModMusicSuppressionIsEnabled() {
        assertTrue(MusicGuardPolicy.isBackground(new BossMusicSound(), true, List.of()));
    }

    @Test
    void recognizedModMusicIsNotBackgroundWhenOtherModMusicSuppressionIsDisabled() {
        assertFalse(MusicGuardPolicy.isBackground(new BossMusicSound(), false, List.of()));
    }

    private static SoundInstance soundInCategory(SoundSource source, String id) {
        SoundInstance sound = mock(SoundInstance.class);
        when(sound.getSource()).thenReturn(source);
        when(sound.getLocation()).thenReturn(ResourceLocation.parse(id));
        return sound;
    }
}
