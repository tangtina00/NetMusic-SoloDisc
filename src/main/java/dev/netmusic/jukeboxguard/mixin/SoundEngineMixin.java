package dev.netmusic.jukeboxguard.mixin;

import dev.netmusic.jukeboxguard.client.ClientMusicGuard;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow
    private float calculateVolume(SoundInstance sound) {
        throw new AssertionError();
    }

    @Shadow
    private float calculateVolume(float volume, SoundSource source) {
        throw new AssertionError();
    }

    @Unique
    private final Set<SoundInstance> jukeboxmusicguard$muted = Collections.newSetFromMap(new IdentityHashMap<>());

    @Unique
    private float jukeboxmusicguard$backgroundVolume(SoundInstance sound, float volume) {
        // A positive floor (-140 dB) keeps silent boss tracks ticking instead of discarding their channels.
        if (ClientMusicGuard.isSuppressing() && ClientMusicGuard.isBackground(sound)) {
            jukeboxmusicguard$muted.add(sound);
            return Math.min(volume, 0.0000001F);
        }
        return volume;
    }

    @Inject(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At("RETURN"), cancellable = true)
    private void jukeboxmusicguard$muteBackground(SoundInstance sound, CallbackInfoReturnable<Float> ci) {
        ci.setReturnValue(jukeboxmusicguard$backgroundVolume(sound, ci.getReturnValue()));
    }

    @Redirect(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngine;calculateVolume(FLnet/minecraft/sounds/SoundSource;)F"))
    private float jukeboxmusicguard$initialVolume(SoundEngine engine, float volume, SoundSource source, SoundInstance sound) {
        return jukeboxmusicguard$backgroundVolume(sound, calculateVolume(volume, source));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void jukeboxmusicguard$refreshChannels(boolean paused, CallbackInfo ci) {
        if (ClientMusicGuard.isSuppressing() || !jukeboxmusicguard$muted.isEmpty()) {
            // Refresh non-tickable tracks too, and use current user volumes when restoring them.
            instanceToChannel.forEach((sound, handle) -> {
                boolean mute = ClientMusicGuard.isSuppressing() && ClientMusicGuard.isBackground(sound);
                if (mute || jukeboxmusicguard$muted.remove(sound)) {
                    float volume = calculateVolume(sound);
                    handle.execute(channel -> channel.setVolume(volume));
                    if (mute) {
                        jukeboxmusicguard$muted.add(sound);
                    }
                }
            });
            jukeboxmusicguard$muted.retainAll(instanceToChannel.keySet());
        }
    }
}
