# Jukebox Music Guard

Minecraft 1.21.1 / NeoForge client patch for NetMusic backpack records.

Install the built jar alongside the official mods on the client. No server patch is needed.

Target versions:

- NetMusic `1.5.2-neoforge+mc1.21.1`
- Sophisticated Core `1.5.2.2343`
- Sophisticated Backpacks `3.26.6.2174`

The patch suppresses background music only after a NetMusic backpack stream starts successfully
and while it is active and audible. Local backpack records have no distance attenuation;
other players' and placed backpacks use the game's actual attenuation range.
Failed downloads, stopped records, muted records, leaving range, logging out, and sound reloads
release suppression. The official mods still own record playback and networking.

All `MUSIC` sounds and Cataclysm's `BossMusicSound` are suppressed by default. Existing tracks
keep their playback state and advance silently; they become audible again at their current
position. Ordinary effects and other records continue to play. Vanilla music scheduling pauses
while a record is audible, then resumes normally. Audio option values are never changed.

Configuration: `config/jukeboxmusicguard-client.toml`

- `enabled`: enable background music suppression.
- `suppressCataclysmBossMusic`: include Cataclysm boss music (uses `RECORDS`).
- `extraBackgroundSounds`: exact event IDs for other mods using a category besides `MUSIC`,
  e.g. `["anothermod:boss_theme"]`. No broad sound-name matching is used.

Compatibility covers music routed through Minecraft's sound engine. Mods using an independent
audio system need their own integration. NetMusic's standalone player is outside this patch's scope.

Build with an existing Java 21 JDK and `./gradlew build`.
Automatic JDK downloads are disabled. If Java 21 is outside Gradle's detected paths, pass
`-Porg.gradle.java.installations.paths=<path-to-existing-jdk>`.

Use an existing Java 21 JDK. On Windows, a Minecraft launcher's bundled Java 21 may also
work if its directory contains both `bin/java.exe` and `bin/javac.exe`.
Machine-specific JDK and proxy paths belong in local command arguments or the user's
Gradle configuration, not in tracked project files.

Validation completed: build and 15 unit tests; NeoForge 21.1.229 client startup with the
target NetMusic and Sophisticated Core jars; all three mixin targets transformed successfully.
The optional `-PsmokeTest` source set forces the otherwise lazily loaded NetMusic backpack
sound class to load. It is excluded from the distributable jar.
The user has also confirmed that the functionality works in their game. Dedicated
Cataclysm combat scenarios have not been separately recorded as verified.

## Project structure

```text
JukeboxMusicGuard/
|-- src/main/java/dev/netmusic/jukeboxguard/
|   |-- JukeboxMusicGuard.java       # Client mod entry point
|   |-- client/                     # Playback tracker and background music policy
|   |-- config/                     # Client configuration defaults
|   `-- mixin/                      # NetMusic, SoundEngine and MusicManager hooks
|-- src/main/resources/
|   |-- META-INF/neoforge.mods.toml # Mod metadata and required dependencies
|   `-- jukeboxmusicguard.mixins.json
|-- src/test/java/                  # 15 unit tests and a Cataclysm test fixture
|-- src/smoke/java/                 # Optional runtime mixin verification
|-- gradle/wrapper/                 # Gradle bootstrap JAR and version configuration
|-- gradlew / gradlew.bat           # Gradle launch scripts
|-- build.gradle                   # Dependencies, compilation, tests and packaging
|-- settings.gradle                # Plugin repositories and project name
|-- gradle.properties              # Portable build options; no automatic JDK download
|-- .gitignore / .gitattributes     # Local file exclusions and line-ending rules
`-- README.md / LICENSE
```

The `BossMusicSound` class under `src/test` is a small test fixture with Cataclysm's
class name. It is not the Cataclysm mod and is not included in the release JAR.
The `src/smoke` source set is enabled only for `runClient -PsmokeTest`.

## Git and local development files

Use this directory as the repository root. The sibling reference mod source trees
are not required to build this patch and need not be included in its repository.

Track the source sets, resources, Gradle configuration, both wrapper scripts,
`gradle-wrapper.jar`, documentation, license, `.gitignore` and `.gitattributes`.
Do not commit `build/`, `.gradle/`, `run/`, `logs/`, `.tools/` or local IDE state.

- `build/`: compiled classes, development libraries, test reports and the built JAR.
  Share a release JAR as a release artifact rather than committing build output.
- `.gradle/`: local incremental build state.
- `run/`: isolated client instance, official mod JARs, generated config, logs and options.
  Keep it locally when continuing runtime testing.
- `.tools/`: disposable downloaded tools; not needed when an existing Java 21 is available.
- The user's global `.gradle/` cache stores reusable Gradle and NeoForge dependencies.
  Keep those dependencies to avoid downloading and generating them for every build.

Configuration defaults are maintained in `ClientConfig.java`; the generated client
TOML in `run/config/` is local runtime data and is not a source file.
