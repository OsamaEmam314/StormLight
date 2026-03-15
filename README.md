# ⚡ StormLight — Precision Weather

A modern Android weather application built with **Kotlin + Jetpack Compose**, following **MVVM architecture** and **Material Design 3** guidelines.

---

## Features

- **Home Screen** — Current weather with temperature, humidity, wind speed, pressure, visibility, and cloud coverage. Includes hourly forecast for today and a 5-day outlook.
- **Favorites Screen** — Save multiple locations via map or city search. Tap any favorite to view its full forecast details.
- **Weather Alerts Screen** — Schedule custom weather alerts with configurable duration and notification type (notification or alarm sound). Alerts are managed via WorkManager.
- **Settings Screen** — Customize location source (GPS / Map), temperature unit (°C / °F / K), wind speed unit (m/s / mph), app language (Arabic / English), and theme (Light / Dark / System). All preferences are persisted with DataStore.

---

## Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | MVVM (No Domain Layer) |
| Networking | Retrofit + GsonConverter |
| Local DB | Room |
| Preferences | DataStore |
| Async | Coroutines + Flow + StateFlow + SharedFlow |
| Background Work | WorkManager |
| Image Loading | Coil |
| Testing | JUnit + MockK |

---

## API

Uses [OpenWeatherMap](https://openweathermap.org/) free tier:

- `GET /data/2.5/weather` — Current weather
- `GET /data/2.5/forecast` — 5-day / 3-hour forecast
- `GET /geo/1.0/direct` — City search (geocoding)
- `GET /geo/1.0/reverse` — Reverse geocoding

Supports `units` (metric / imperial / standard) and `lang` (en / ar) parameters.

---

## Project Structure
```
app/src/main/java/com/example/stormlight/
├── data/
│   ├── db/                  # Room database & DAOs
│   ├── datastore/           # DataStore wrappers
│   ├── model/               # Data models & entities
│   ├── network/             # Retrofit client & API service
│   ├── weather/
│   │   ├── local/           # WeatherLocalDataSource (interface + impl)
│   │   ├── remote/          # WeatherRemoteDataSource (interface + impl)
│   │   └── repository/      # WeatherRepository (interface + impl)
│   └── preferences/
│       ├── local/           # PreferencesLocalDataSource (interface + impl)
│       └── repository/      # PreferencesRepository (interface + impl)
├── ui/
│   ├── main/                # MainActivity + MainViewModel
│   ├── components/          # Shared composables
│   ├── theme/               # Material3 theme
│   └── screens/
│       ├── home/            # HomeScreen + HomeViewModel + Factory
│       ├── favorites/       # FavoritesScreen + ViewModel + Factory
│       ├── details/         # FavoriteDetailScreen + ViewModel + Factory
│       ├── alerts/          # AlertsScreen + ViewModel + Factory
│       ├── settings/        # SettingsScreen + ViewModel + Factory
│       └── map/             # MapPickerActivity
├── utilities/               # Constants, extension functions, enums, unit converters
├── alarmmanager/            # WorkManager workers & factories
└── StormLightApplication.kt
```

---

## Localization

The app fully supports **Arabic (RTL)** and **English (LTR)**. Language is changed at runtime from Settings using DataStore and applied via `AppCompatDelegate`.

---

## Architecture Notes

- No Domain Layer — ViewModels interact directly with Repository interfaces
- All repositories and data sources are behind interfaces for testability
- Every ViewModel has a dedicated `ViewModelProvider.Factory` declared inside its screen file
- `!!` (non-null assertion) is never used throughout the codebase
- Preferences (theme, language, units, location) are all stored in DataStore and exposed as `Flow`

---

## Getting Started

1. Clone the repo
2. Add your OpenWeatherMap API key to `local.properties`:
```
   WEATHER_API_KEY=your_key_here
```
3. Build and run on Android 8.0+ (API 26+)

---

## Unit Tests

Unit tests cover ViewModels using MockK and `kotlinx-coroutines-test`. Tests are located under `app/src/test/`.
