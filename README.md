# Quantiq Android App

Quantiq is a Kotlin/Jetpack Compose Android application that implements a counter tracking flow with
notifications, billing, backups, and a Glance widget. The project follows a lightweight layered
architecture (data/domain/ui) with a small DI container and MVI-style view models.

## Project structure

```
.
├── app
│   ├── src/main
│   │   ├── java/com/example/quantiq
│   │   │   ├── billing
│   │   │   ├── data
│   │   │   ├── di
│   │   │   ├── domain
│   │   │   ├── mvi
│   │   │   ├── notifications
│   │   │   ├── ui
│   │   │   └── widget
│   │   └── res
│   └── src/test
│       └── java/com/example/quantiq
├── gradle
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

### App module (`app`)

**`billing/`**
- `BillingManager`: wraps Play Billing client state and exposes purchase flow events.

**`data/`**
- `CounterEntity`: Room entity representing a persisted counter row.
- `QuantiqDatabase`: Room database holder for counters and notifications.
- `BackupManager`: JSON import/export utility for counters.
- `mapper/CounterMapper`: maps between database entities and domain models.
- `notification/ItemNotificationConfigEntity`: Room entity for notification settings.
- `notification/ItemNotificationConfigDao`: DAO for notification config CRUD.
- `notification/ItemNotificationConfigMapper`: maps notification configs between layers.
- `notification/NotificationJsonAdapter`: Gson adapter for serializing notification configs.
- `repository/CounterRepositoryImpl`: data-layer implementation of counter repository.
- `repository/ItemNotificationRepositoryImpl`: data-layer implementation of notification repository.

**`di/`**
- `AppContainer`: lightweight dependency wiring (repositories, schedulers, use cases, managers).

**`domain/`**
- `model/Counter`: domain representation of a counter.
- `model/ItemNotificationConfig`: domain representation of notification configuration.
- `model/NotificationAction`: enum describing notification click actions.
- `model/UpcomingNotification`: upcoming scheduled notification model.
- `notification/NotificationScheduler`: interface for scheduling/canceling notifications.
- `notification/NotificationScheduleCalculator`: calculates upcoming schedules.
- `repository/CounterRepository`: domain contract for counter storage.
- `repository/ItemNotificationRepository`: domain contract for notification config storage.
- `usecase/*`: single-responsibility use cases for counters and notifications (observe, update,
  reset, schedule, enable/disable, etc.).

**`mvi/`**
- `MviViewModel`: base class for MVI view models with state/effect flows.
- `UiState`, `UiIntent`, `UiEffect`: marker contracts for MVI layers.

**`notifications/`**
- `NotificationConstants`: shared constants (IDs, channels, extras).
- `NotificationChannels`: creates Android notification channels.
- `LocalNotificationScheduler`: WorkManager-backed scheduler implementation.
- `ItemNotificationWorker`: worker that delivers scheduled notifications.
- `NotificationActionReceiver`: broadcast receiver for notification actions.

**`ui/`**
- `MainActivity`: app entry activity hosting Compose content.
- `AppRoot`: Compose root that wires navigation and top-level UI.
- `MainViewModel`: main screen state management (counters/billing/backup).
- `MainViewModelFactory`: ViewModel factory for `MainViewModel`.
- `ItemNotificationViewModel`: view model for per-item notification settings.
- `ItemNotificationViewModelFactory`: factory for notification view model.
- `navigation/NavRoutes`: typed routes and navigation helpers.
- `screens/`: Compose screens for list/detail/settings/guidelines, including step selection and reset flows.
- `components/`: reusable UI elements like the convex bottom bar and elevated circular buttons.
- `settings/notifications/`: notification settings screens and view models.
- `theme/Theme`: Compose Material3 theme setup.

**`widget/`**
- `CounterWidget`: Glance app widget presenting counter data.

### Tests (`app/src/test`)
- `domain/usecase/*Test`: verifies use case behavior with fakes.
- `ui/navigation/NavRoutesTest`: validates route formatting.
- `domain/usecase/Fakes`: test doubles for repositories and schedulers.

## Build and run

1. Open the project in Android Studio.
2. Use a locally installed Gradle or regenerate the wrapper if needed.
3. Build or run:
   - `gradle assembleDebug`
   - `gradle installDebug`

## Compose compiler configuration

The project uses the Kotlin Compose compiler Gradle plugin (required for Kotlin 2.0+ when Compose
is enabled). See `app/build.gradle.kts` for the applied plugin and `gradle/libs.versions.toml` for
version management.
