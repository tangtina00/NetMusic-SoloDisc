package dev.netmusic.solo_disc.client;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientMusicGuardTest {
    private static final Vec3 LISTENER = new Vec3(0, 0, 0);

    @Test
    void relativeSoundIsAudibleWithoutResolvingAnAttenuationRange() {
        SoundInstance sound = playableSound(1);
        when(sound.isRelative()).thenReturn(true);
        when(sound.getX()).thenReturn(10000d);

        assertTrue(ClientMusicGuard.isAudible(sound, LISTENER));
    }

    @Test
    void unattenuatedSoundIsAudibleWithoutResolvingAnAttenuationRange() {
        SoundInstance sound = playableSound(1);
        when(sound.getAttenuation()).thenReturn(SoundInstance.Attenuation.NONE);
        when(sound.getX()).thenReturn(10000d);

        assertTrue(ClientMusicGuard.isAudible(sound, LISTENER));
    }

    @Test
    void unresolvedLinearSoundIsNotAudible() {
        assertFalse(ClientMusicGuard.isAudible(playableSound(1), LISTENER));
    }

    @Test
    void linearSoundIsAudibleOnlyInsideItsAttenuationRange() {
        SoundInstance sound = attenuatedSound(1, 16);
        when(sound.getX()).thenReturn(15.999d);
        assertTrue(ClientMusicGuard.isAudible(sound, LISTENER));

        when(sound.getX()).thenReturn(16d);
        assertFalse(ClientMusicGuard.isAudible(sound, LISTENER));

        when(sound.getX()).thenReturn(16.001d);
        assertFalse(ClientMusicGuard.isAudible(sound, LISTENER));
    }

    @Test
    void highVolumeExtendsLinearAttenuationRange() {
        SoundInstance sound = attenuatedSound(2, 16);
        when(sound.getX()).thenReturn(31.999d);
        assertTrue(ClientMusicGuard.isAudible(sound, LISTENER));

        when(sound.getX()).thenReturn(32d);
        assertFalse(ClientMusicGuard.isAudible(sound, LISTENER));
    }

    @Test
    void lowPositiveVolumeKeepsMinimumLinearAttenuationRange() {
        SoundInstance sound = attenuatedSound(0.25f, 16);
        when(sound.getX()).thenReturn(15d);

        assertTrue(ClientMusicGuard.isAudible(sound, LISTENER));
    }

    @Test
    void distanceIncludesAllThreeAxesAndListenerPosition() {
        SoundInstance sound = attenuatedSound(1, 5);
        when(sound.getX()).thenReturn(10d);
        when(sound.getY()).thenReturn(23d);
        when(sound.getZ()).thenReturn(34d);

        assertFalse(ClientMusicGuard.isAudible(sound, new Vec3(10, 20, 30)));
        assertTrue(ClientMusicGuard.isAudible(sound, new Vec3(10, 20.1, 30)));
    }

    @Test
    void zeroAndNegativeVolumeAreNotAudibleEvenWhenRelative() {
        SoundInstance sound = playableSound(0);
        when(sound.isRelative()).thenReturn(true);
        assertFalse(ClientMusicGuard.isAudible(sound, LISTENER));

        when(sound.getVolume()).thenReturn(-1f);
        assertFalse(ClientMusicGuard.isAudible(sound, LISTENER));
    }

    @Test
    void soundThatCannotPlayIsNotAudibleEvenWithoutAttenuation() {
        SoundInstance sound = playableSound(1);
        when(sound.canPlaySound()).thenReturn(false);
        when(sound.getAttenuation()).thenReturn(SoundInstance.Attenuation.NONE);

        assertFalse(ClientMusicGuard.isAudible(sound, LISTENER));
    }


    private static SoundInstance playableSound(float volume) {
        SoundInstance sound = mock(SoundInstance.class);
        when(sound.canPlaySound()).thenReturn(true);
        when(sound.getVolume()).thenReturn(volume);
        when(sound.getAttenuation()).thenReturn(SoundInstance.Attenuation.LINEAR);
        return sound;
    }

    private static SoundInstance attenuatedSound(float volume, int attenuationDistance) {
        SoundInstance sound = playableSound(volume);
        Sound resolved = mock(Sound.class);
        when(resolved.getAttenuationDistance()).thenReturn(attenuationDistance);
        when(sound.getSound()).thenReturn(resolved);
        return sound;
    }

}
