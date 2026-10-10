package dev.netmusic.solo_disc.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue SUPPRESS_OTHER_MOD_MUSIC;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXTRA_BACKGROUND_SOUNDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ENABLED = builder.comment("Suppress background music while an audible NetMusic backpack record is playing.")
                .define("enabled", true);
        SUPPRESS_OTHER_MOD_MUSIC = builder.comment("Try to suppress recognized mod background music outside the MUSIC category.",
                        "Currently supports Cataclysm boss music. This does not suppress every RECORDS sound.")
                .define("suppressOtherModMusic", true);
        EXTRA_BACKGROUND_SOUNDS = builder.comment("Exact sound event IDs of other background music outside the MUSIC category.",
                        "Example: anothermod:boss_theme. Ordinary sound effects are not suppressed.")
                .defineListAllowEmpty("extraBackgroundSounds", List.of(), () -> "anothermod:boss_theme",
                        value -> value instanceof String id && ResourceLocation.tryParse(id) != null);
        SPEC = builder.build();
    }

    private ClientConfig() {
    }
}
