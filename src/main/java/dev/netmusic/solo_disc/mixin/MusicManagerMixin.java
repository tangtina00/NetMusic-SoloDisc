package dev.netmusic.solo_disc.mixin;

import dev.netmusic.solo_disc.client.ClientMusicGuard;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void netmusic_solo_disc$holdScheduling(CallbackInfo ci) {
        if (ClientMusicGuard.isSuppressing()) {
            ci.cancel();
        }
    }
}
