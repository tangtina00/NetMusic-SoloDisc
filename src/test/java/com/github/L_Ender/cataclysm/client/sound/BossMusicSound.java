package com.github.L_Ender.cataclysm.client.sound;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

// Preserve the integration's exact runtime class name without adding Cataclysm as a dependency.
public final class BossMusicSound extends AbstractSoundInstance {
    public BossMusicSound() {
        super(ResourceLocation.parse("cataclysm:monstrosity_music"), SoundSource.RECORDS, RandomSource.create(0));
    }
}
