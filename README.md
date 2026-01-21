# Quantiq - Android Skeleton

This repository contains the Kotlin source code for the Quantiq Android app skeleton, wired with
Jetpack Compose, Navigation Compose, Room, and a lightweight `AppContainer` dependency setup.

## Current behavior
- Launches into a real Compose entry point (`AppRoot`) that hosts the navigation graph.
- Default start destination is the counter list screen.
- Navigation routes cover counter details, settings, notification settings/detail flows, upcoming
  schedules, and Google guidelines screens.

## Structure
- `data/`: Room entities, DAOs, and repository implementation.
- `domain/`: Domain models and use cases.
- `di/`: Simple app container for wiring dependencies.
- `ui/`: Jetpack Compose screens, navigation, and ViewModels.
- `widget/`: Jetpack Glance app widget implementation.

## How to run
1. Open the project in Android Studio.
2. Use a locally installed Gradle (the wrapper JAR is intentionally not committed).
3. Build or run with:
   - `gradle assembleDebug` or
   - `gradle installDebug`

## Dependency notes
Add the following dependencies to your `build.gradle.kts` (app module) if they are not already
present:

```kotlin
dependencies {
    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Glance (Widgets)
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
}
```

## Notes
- This repository does not commit `gradle/wrapper/gradle-wrapper.jar` because some PR systems reject binary files.
  Use a locally installed Gradle to build (for example, `gradle assembleDebug`) or to regenerate the wrapper
  (`gradle wrapper`).
- The app uses a simple `AppContainer` to wire dependencies; if you migrate to Hilt, you can replace it cleanly.
