Branchster-Android
==================

## Configuring Keys and Secrets
This repository does not contain API keys so you need to define your own in order for the connected APIs to function. With the exception of the *Crashlytics ApiKey* (see the note below) the keys are defined as XML string resources and referenced at build-time. If you build the project as-is, you will get something like the following error:

```
Error: .. No resource found that matches the given name (at 'value' with value '@string/..').
```

To set up your own API keys and get rid of this error:

1. Open up **api_keys.xml** which exists in the */res/values* folder.
2. Insert your Branch App Key, Facebook ID and Twitter key/secret in this file.
3. Clean/Rebuild your project.

```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--
    Your Branch App Key Goes Here
    If you don't have one, see the Branch Android Quick-Start for how to get one:
    https://github.com/BranchMetrics/Branch-Integration-Guides/blob/master/android-quick-start.md
    -->
    <string name="bnc_app_key">YOUR BRANCH APP KEY</string>

    <!--
    Your Your Facebook App ID Goes Here
    If you don't have one, see the Facebook SDK for Android documentation:
    https://developers.facebook.com/docs/android
    -->
    <string name="facebook_app_id">YOUR FACEBOOK APP ID</string>

    <!--
    Your Twitter Key and Secret Goes Here
    If you don't have these, see the Twitter Kit for Android documentation:
    https://dev.twitter.com/twitter-kit/android
    -->
    <string name="twitter_key">YOUR TWITTER APP KEY</string>
    <string name="twitter_secret">YOUR TWITTER APP SECRET</string>

</resources>
```

### Fabric/Crashlytics (required for Twitter integration)

Twitter's Fabric framework doesn't currently allow the *com.crashlytics.ApiKey* meta-data to be specified as a String resource in the *ApplicationManifest.xml* file. If you try to add the key as a *@string/..* reference you will get a *Crashlytics Developer Tools error* at build time.

So to get Twitter integration working, you will need to insert your Crashyltics key directly in *AndroidManifest.xml* like so:

```XML
<manifest .. >
  <application .. />
  
    <activity .. />

    <meta-data 
      android:name="com.crashlytics.ApiKey"
      android:value="YOUR FABRIC/CRASHLYTICS ApiKey" />
      
      --
      
  </application>
</manifest>
```
<img width="200" height="480" alt="Android_1" src="https://github.com/user-attachments/assets/a6233f3e-79e8-417a-8e51-00ece8bcbb56" />
<img width="200" height="480" alt="Android_2" src="https://github.com/user-attachments/assets/54eaecbf-7fef-4709-9fc0-bfac68aec7c5" />
<img width="200" height="480" alt="Android_3" src="https://github.com/user-attachments/assets/53340ec8-6411-49ce-b8f7-65a197ed2e94" />
<img width="200" height="480" alt="Android_4" src="https://github.com/user-attachments/assets/ac465707-2462-4acc-a639-c09b606fe321" />

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
