# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ChickenFeather is a NeoForge Minecraft mod (1.21.4) that lets players right-click chickens to collect feathers without killing them. Feather drops include experience rewards and have a 6000-tick (~5 minute) cooldown per chicken using entity tags.

## Build Commands

```bash
./gradlew build              # Build the mod JAR
./gradlew runClient          # Launch Minecraft client with mod loaded
./gradlew runServer          # Launch dedicated server with mod loaded
./gradlew runGameTestServer  # Run game test server
./gradlew runClientData      # Run data generation
./gradlew --refresh-dependencies  # Fix missing libraries/dependencies
./gradlew clean              # Reset build outputs
```

Requires **Java 21** (Temurin recommended).

## Architecture

This is a single-class mod. All logic lives in `src/main/java/co/lemee/chickenfeathermod/ChickenFeatherMod.java`.

**Mod ID:** `chickenfeathermod` (must match `@Mod` annotation, `gradle.properties`, and `neoforge.mods.toml`)

**Event-driven design using NeoForge event bus:**
- `PlayerInteractEvent.EntityInteract` - Handles right-click on chickens: drops feather, awards XP, tags chicken with `chickenfeather:dropped`
- `LivingBreatheEvent` - Cooldown timer: removes dropped tag every 6000 ticks on chickens
- Mod events (`FMLCommonSetupEvent`, `FMLClientSetupEvent`, `ServerStartingEvent`) for lifecycle logging

**No custom registries** - the mod uses only vanilla items (`Items.FEATHER`) and entity tags for state.

## Key Configuration

- `gradle.properties` - Mod metadata, Minecraft/NeoForge versions, Parchment mapping versions
- `src/main/templates/META-INF/neoforge.mods.toml` - Mod loader metadata (uses variable substitution from gradle.properties)
- `src/main/resources/assets/chickenfeathermod/lang/en_us.json` - Localization strings

## Git Commits

- Never mention Claude or add Co-Authored-By lines in commit messages
- Follow conventional commit style: `chore:`, `fix:`, `feat:`, etc.
- Version compatibility commits use: `chore: neoforge {version} compatibility`

## Releasing a New Minecraft Version

### 1. Find the right versions

Check the official NeoForge MDK for the target version:
`https://github.com/NeoForgeMDKs/MDK-{version}-ModDevGradle/blob/main/gradle.properties`

Key properties to update in `gradle.properties`:
- `minecraft_version`, `minecraft_version_range`
- `neo_version`, `neo_version_range`
- `parchment_minecraft_version`, `parchment_mappings_version`
- `loader_version_range`
- `mod_version`

Also check the MDK's `build.gradle` (ModDevGradle plugin version) and `gradle/wrapper/gradle-wrapper.properties` (Gradle version).

### 2. Check for breaking changes

Migration primers are at: `https://docs.neoforged.net/primer/docs/{version}/`

Known breaking changes:
- **1.21.10+**: `@EventBusSubscriber` no longer has `bus` parameter. Remove `bus = EventBusSubscriber.Bus.MOD`. Events are auto-routed to the correct bus.
- **1.21.10+**: Requires Gradle 9.x and ModDevGradle 2.x (was Gradle 8.x and ModDevGradle 1.x)
- **1.21.1**: Uses `loader_version_range=[1,)` instead of `[4,)`

### 3. GitHub Release

```bash
gh release create {version} build/libs/chickenfeathermod-{version}.jar --title "{version}" --notes "**Full Changelog**: https://github.com/jblemee/ChickenFeather/commits/{version}"
```

## Conventions

- NeoForge 1.21.4 APIs with Parchment mappings for human-readable parameter names
- Server-side logic guarded by `level instanceof ServerLevel` checks
- Entity tags (string-based via `Entity.addTag`/`removeTag`/`getTags`) for per-entity state tracking
