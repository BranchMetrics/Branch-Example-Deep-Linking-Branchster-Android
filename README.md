# Branchsters Android App

A modern Android application built with Jetpack Compose and MVVM architecture.

## Features

- **Jetpack Compose UI**: Modern declarative UI toolkit
- **MVVM Architecture**: Clean separation of concerns with ViewModels
- **Navigation Component**: Type-safe navigation between screens
- **Material Design 3**: Beautiful, modern UI components
- **Splash Screen**: Animated splash screen with loading indicator
- **Home Screen**: Welcome screen with card-based layout

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Navigation Compose
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/src/main/java/io/branch/branchsters/
├── MainActivity.kt                 # Main activity entry point
├── navigation/
│   └── NavGraph.kt                # Navigation graph and routes
├── ui/
│   ├── theme/                     # App theme and styling
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── splash/                    # Splash screen
│   │   ├── SplashScreen.kt
│   │   └── SplashViewModel.kt
│   └── home/                      # Home screen
│       ├── HomeScreen.kt
│       └── HomeViewModel.kt
```

## Building the Project

### Prerequisites

- Android Studio Hedgehog or later
- JDK 8 or higher
- Android SDK with API level 34

### Build Instructions

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

Or use the command line:

```bash
./gradlew assembleDebug
```

## Running the App

```bash
./gradlew installDebug
```

## Package Name

`io.branch.branchsters`

## Screens

### Splash Screen
- Displays app title with fade-in animation
- Shows loading indicator
- Automatically navigates to Home screen after 2 seconds

### Home Screen
- Welcome message
- Card-based layout with Material Design 3
- Custom Branchsters theme colors

## Customization

### Colors
Edit `app/src/main/java/io/branch/branchsters/ui/theme/Color.kt` to customize the color scheme.

### Theme
Modify `app/src/main/java/io/branch/branchsters/ui/theme/Theme.kt` to adjust the overall theme.

## License

Copyright © 2025 Branch
