# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Android application built with Kotlin and Jetpack Compose. Application code lives under `app/src/main/java/com/chlqudco/flash/`: `MainActivity.kt` manages camera permission and torch state, `ui/` contains Compose screens and theme definitions, and `ads/` contains AdMob integration. Android resources are in `app/src/main/res/`, with text in `values/strings.xml` and launcher assets in `drawable/` and `mipmap-*`. Local unit tests belong in `app/src/test/`; device and UI tests belong in `app/src/androidTest/`. Dependency versions are centralized in `gradle/libs.versions.toml`.

## Build, Test, and Development Commands

Run commands from the repository root. On macOS or Linux, replace `gradlew.bat` with `./gradlew`.

- `.\gradlew.bat :app:assembleDebug` builds the debug APK.
- `.\gradlew.bat :app:testDebugUnitTest` runs local JVM tests.
- `.\gradlew.bat :app:connectedDebugAndroidTest` runs instrumentation and Compose UI tests on a connected device or emulator.
- `.\gradlew.bat :app:lintDebug` performs Android static analysis.
- `.\gradlew.bat :app:installDebug` installs the debug build on a connected device.

Before submitting changes, run the unit tests, lint, and debug build.

## Coding Style & Naming Conventions

Follow Kotlin's official style with four-space indentation and trailing commas where they improve diffs. Use `PascalCase` for classes and composables, `camelCase` for functions and properties, and `snake_case` for resource names. Keep Android hardware control in the activity/controller layer and keep composables focused on rendering state and emitting events. Do not add code comments or KDoc unless explicitly requested. Preserve existing comments unless a code change requires a minimal correction.

## Testing Guidelines

Use JUnit 4 for local tests and AndroidX JUnit, Espresso, or Compose UI testing for device tests. Name tests after observable behavior, such as `toggle_whenPermissionGranted_enablesTorch`. There is no numeric coverage threshold; add focused tests for new state transitions, permission behavior, and UI interactions.

## Commit & Pull Request Guidelines

The history currently contains only `Initial commit`, so no formal convention is established. Use short, imperative subjects such as `Add torch availability handling`. Keep commits scoped to one concern. Pull requests should explain behavior changes, list verification commands, link related issues, and include screenshots for UI changes. Note any real-device limitations or AdMob configuration changes.

## Security & Configuration

Keep `local.properties`, signing keys, and credentials out of version control. Development builds must use Google's test AdMob IDs; do not exercise live ads during development.
