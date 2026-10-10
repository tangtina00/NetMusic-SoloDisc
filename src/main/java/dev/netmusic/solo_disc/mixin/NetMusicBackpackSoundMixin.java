package dev.netmusic.solo_disc.mixin;

import dev.netmusic.solo_disc.client.NetworkRecord;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(targets = "com.github.tartaricacid.netmusic.compat.sbackpack.NetMusicBackpackSound", remap = false)
public class NetMusicBackpackSoundMixin implements NetworkRecord {
    @Unique
    private volatile boolean netmusic_solo_disc$streamReady;

    @Inject(method = "getStream", at = @At("RETURN"), cancellable = true)
    private void netmusic_solo_disc$observeStream(SoundBufferLibrary buffers, Sound sound, boolean looping,
            CallbackInfoReturnable<CompletableFuture<AudioStream>> ci) {
        ci.setReturnValue(ci.getReturnValue().thenApply(stream -> {
            // NetMusic returns a local error sound after a failed download; that must not suppress music.
            netmusic_solo_disc$streamReady = stream.getClass().getName()
                    .equals("com.github.tartaricacid.netmusic.client.audio.NetMusicAudioStream");
            return stream;
        }));
    }

    @Override
    public boolean netmusic_solo_disc$isStreamReady() {
        return netmusic_solo_disc$streamReady;
    }
}
