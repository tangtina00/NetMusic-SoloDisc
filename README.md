# NetMusic:Solo Disc

[简体中文](README.zh-CN.md) | English

A client-side patch for Minecraft 1.21.1 / NeoForge that prevents background music
from overlapping with NetMusic records played through Sophisticated Backpacks.

**0.1.0-alpha (test release)**

## Features

- Suppresses vanilla background music while an audible NetMusic backpack record is playing.
- Covers other mods using the `MUSIC` category, with built-in support for Cataclysm boss music.
- Releases suppression when playback stops or you leave its audible range. Background tracks
  still playing become audible at their current position.

Suppression starts only after a stream successfully starts. Your audio settings remain unchanged.

## Target setup:

Target setup:

| Component | Version |
| --- | --- |
| NeoForge | 21.1.229 |
| NetMusic | 1.5.2-neoforge+mc1.21.1 |
| Sophisticated Core | 1.5.2.2343 |
| Sophisticated Backpacks | 3.26.6.2174 |

## Configuration and compatibility

Configuration: `config/NetMusicSoloDisc-client.toml`.

- `enabled`: enable background music suppression; defaults to `true`.
- `suppressOtherModMusic`: try to suppress mod music outside the `MUSIC` category using
  built-in compatibility rules; defaults to `true`. Currently covers Cataclysm boss music
  in `RECORDS`, without muting the entire `RECORDS` category.
- `extraBackgroundSounds`: additional exact sound event IDs, e.g. `["anothermod:boss_theme"]`.
  These apply independently of `suppressOtherModMusic` when the patch is enabled.

Only NetMusic records in backpack jukebox upgrades activate this patch. Vanilla records and
NetMusic's standalone player do not activate it. Music using Minecraft's `MUSIC` category is
covered automatically. Other categories may need additional IDs; separate audio systems
need dedicated integration.

Please include mod versions and reproduction steps in bug reports.

unofficial compatibility patch. Licensed under [MIT](LICENSE).
