# Quantiq - Android Skeleton

This folder contains the Kotlin source code for the native Android application.

## Structure
- `data/`: Room Database Entity and DAO.
- `ui/`: Jetpack Compose Screens and ViewModel (MVVM).
- `widget/`: Jetpack Glance App Widget implementation.

## How to use
1. Create a new "Empty Activity" project in Android Studio.
2. Select "Kotlin" and "Jetpack Compose".
3. Copy these files into your `src/main/java/com/example/quantiq` directory.
4. Add the following dependencies to your `build.gradle.kts` (app module):

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

## Note
This is a generated skeleton. You will need to wire up the `MainActivity` to instantiate the `Room` database and pass the `Dao` to the `ViewModelFactory`.
