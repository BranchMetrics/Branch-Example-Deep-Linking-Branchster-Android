# Branchster (Android) - Claude Context & Rules

## Project Overview
* **Purpose:** Branch's example/demo Android app ("Branchster") showcasing Branch SDK deep linking, deep link routing, QR code link generation, and custom event tracking, wrapped in a "monster factory" game-like UI (create a monster, complete quests, share a Branch link/QR code, and deep link back into the app to a Details screen).
* **Tech Stack:** Kotlin, Jetpack Compose (Material 3), Room (local persistence), Navigation Compose, Firebase AI Logic SDK (Gemini Developer API, free tier) for monster image generation, Branch Android SDK.
* **Architecture Pattern:** Single-module Android app using MVVM — `views/` (Composable screens) + `viewmodels/` (ViewModel + StateFlow `UiState`) + `data/repository/` (Room-backed repositories) + `data/dao` / `data/entity` (Room).
* **Package name:** `io.branch.branchster` (note: README and some resource files still say `branchsters` — see Known Gotchas).

---

## Quick Reference Commands

### Development
* Build debug APK: `./gradlew assembleDebug`
* Install debug build on device/emulator: `./gradlew installDebug`
* Clean build: `./gradlew clean`
* Open in Android Studio (Hedgehog+) and let Gradle sync — this is the primary supported workflow for this repo.

### Testing
* Run unit tests: `./gradlew test`
* Run instrumented/UI tests (Espresso/Compose UI test): `./gradlew connectedAndroidTest`

### Code Quality & Linting
* Lint: `./gradlew lint`
* There is a `lint.xml` at the repo root — check it before adding new lint suppressions.
* No ktlint/detekt config is present in this repo; match existing Kotlin style already in `app/src/main/java/io/branch/branchster`.

---

## Directory & File Structure

```
[root]/
├── app/
│   ├── build.gradle.kts              # Module config: applicationId io.branch.branchster, minSdk 24, targetSdk/compileSdk 35
│   ├── google-services.json          # Firebase config, committed to the repo (demo app only)
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # Branch deep link intent-filters, BranchKey meta-data, FileProvider
│       │   ├── java/io/branch/branchster/
│       │   │   ├── ApplicationClass.kt        # Application: Branch.getAutoInstance(), Room DB + repos, SoundManager
│       │   │   ├── MainActivity.kt            # Branch.sessionBuilder().init()/reInit(), splash-screen gating, deep link routing to Details
│       │   │   ├── components/                # Small reusable Compose widgets
│       │   │   ├── data/
│       │   │   │   ├── entity/                # Room entities: Monster, Quest, BranchEventData
│       │   │   │   ├── dao/                    # Room DAOs
│       │   │   │   └── repository/             # MonsterRepository, QuestRepository, BranchEventRepository
│       │   │   ├── manager/SoundManager.kt
│       │   │   ├── models/                     # UI state + Firebase AI image-generation request/response models
│       │   │   ├── navigation/NavGraph.kt      # Screen routes: Splash, Onboarding, Home, CreateLink, Logs, Details(deeplink)
│       │   │   ├── viewmodels/                 # HomeViewModel, OnboardingViewModel, SplashViewModel
│       │   │   └── views/                      # Compose screens + views/homeComponents overlays (QR code, share link, trigger event, view Branch data)
│       │   └── res/                            # Compose theme also lives partly in ui/theme alongside res/
│       ├── android/res/values/api_keys.xml     # LEGACY — see Known Gotchas, not part of the active build
│       └── amazon/                             # LEGACY Amazon flavor (AndroidManifest.xml, api_keys.xml) — see Known Gotchas
├── kit-libs/                                    # LEGACY Twitter/Digits/Fabric SDK libraries — see Known Gotchas
└── kits.properties                              # LEGACY Twitter Fabric kit descriptor
```

---

## Coding Standards & Conventions

### Language & Syntax
* Kotlin throughout the active app code; one legacy Java file (`fragment/InfoFragment.java`) remains from an older version — don't use it as a style reference for new code.
* Jetpack Compose declarative UI; `ComponentActivity` + `setContent { }`, no XML layouts for new screens (the `res/layout/*.xml` files that exist are legacy/unused by current screens).
* State held in `ViewModel`s as `MutableStateFlow<XyzUiState>` exposed via `asStateFlow()`, combined with `combine {}` from repository flows.

### Architecture & Patterns
* **Persistence:** Room only (`AppDatabase`, `*Dao`, `*Repository`). Access data through the repository, not the DAO directly, from ViewModels.
* **Branch SDK lifecycle:** `Branch.getAutoInstance(this)` is initialized once in `ApplicationClass.onCreate()`. Session init/re-init (`Branch.sessionBuilder(this).withCallback{...}.withData(intent?.data).init()`) happens in `MainActivity.onStart()`, and `reInit()` in `onNewIntent()` when `branch_force_new_session` is set. Keep new deep-link handling consistent with this pattern rather than introducing a second init path.
* **Deep link → UI:** `MainActivity` decodes `+clicked_branch_link` from the Branch callback, stores it in `branchData` (Compose state), and a `LaunchedEffect` navigates to `Screen.Details.createRoute(...)`. New deep-link-driven screens should follow this same state → `LaunchedEffect` → `navController.navigate` flow.
* **Navigation:** All routes are declared as `Screen` sealed subclasses in `navigation/NavGraph.kt`. Add new destinations there rather than hardcoding route strings elsewhere.
* **Image generation:** Firebase AI Logic (Gemini Developer API free tier) is used for monster image generation via `models/ImageGenerationRequest`/`ImageGenerationResponse`. Firebase is configured via `app/google-services.json`.

### Naming Conventions
* **Files:** `PascalCase.kt` for classes/composables/screens (matches Kotlin convention, not the generic `kebab-case` file rule).
* **Variables/Functions:** `camelCase`.
* **Types/Interfaces:** `PascalCase`, no `I` prefix on interfaces.
* **Compose screens:** suffixed `...Screen.kt`; smaller sub-components under `views/homeComponents/` suffixed `...Overlay.kt`.

---

## Testing Policy
* **Frameworks present:** JUnit4 for unit tests; Espresso + Compose UI Test (`ui-test-junit4`, `ui-tooling`) for instrumented tests.
* **Current coverage:** This is a demo/example app — there is little to no existing test coverage. Don't assume test scaffolding exists for a given class; check before writing new tests against it.
* Favor testing ViewModel `UiState` transformations over full Compose UI trees where possible, given the MVVM structure.

---

## Environment & Prerequisites
* **Android Studio:** Hedgehog or later.
* **JDK:** 8+ (module `sourceCompatibility`/`targetCompatibility` = 1.8, though Gradle wrapper itself is 8.13 and can run on a newer JDK).
* **compileSdk/targetSdk:** 35; **minSdk:** 24.
* **Required keys:** This repo ships without real third-party secrets checked into `app/src/main/res/values/strings.xml` beyond the demo Branch keys already present. Per `README.md`, keys are configured in `api_keys.xml`-style string resources (`bnc_app_key`/`branch_key`, `facebook_app_id`, `twitter_key`/`twitter_secret`) and, for Crashlytics/Fabric, directly in `AndroidManifest.xml` as `com.crashlytics.ApiKey` (cannot be a `@string` reference).
* `app/google-services.json` (Firebase) is committed for this demo app — treat as a template, don't assume it's a private secret worth protecting further, but also don't casually replace it with a personal Firebase project's file in shared branches.

---

## Known Gotchas / Legacy Cruft (read before touching build config)
* **Legacy Twitter/Fabric/Digits SDKs:** `kit-libs/`, `kits.properties`, and the Fabric/Crashlytics instructions in `README.md` are leftovers from a much older Twitter-Fabric-based version of this app. There is no `com.crashlytics` or Twitter Fabric plugin in the current `app/build.gradle.kts` / top-level `build.gradle.kts`. Don't wire new code to these unless you're deliberately reviving that integration — they are dead weight today.
* **Legacy `android` and `amazon` source sets:** `app/src/android/` and `app/src/amazon/` (their own `AndroidManifest.xml` and `api_keys.xml`) reference classes (`BranchsterAndroidApplication`, `SplashActivity`) that don't exist in `app/src/main/java`. There is **no `productFlavors`/`flavorDimensions` block** in `app/build.gradle.kts`, so these source sets are never compiled into the app — they're historical artifacts, not an active build variant. Don't assume editing them affects the shipped app.
* **Branch test-key resource name mismatch across flavors:** `app/src/main/AndroidManifest.xml` references `@string/branch_test_key`, which is defined correctly in `app/src/main/res/values/strings.xml`. However, the (inactive) `app/src/android/res/values/api_keys.xml` and `app/src/amazon/res/values/api_keys.xml` define the *same concept* under the reversed name `branch_key_test`. If a `productFlavors` block is ever added back, this naming mismatch will break the manifest merge for those flavors — rename consistently before reviving them.
* **Package naming inconsistency:** The actual package/applicationId is `io.branch.branchster` (singular "branchster"), but `README.md`'s "Project Structure" section, the `Screen.CreateLink` comment context, and some drawable/manifest labels use `Branchsters`/`branchsters` (plural). Don't trust the README's file paths literally — verify against `app/src/main/java/io/branch/branchster/` directly.
* **`app/src/main/java/io/branch/branchster/fragment/InfoFragment.java`** and the `res/layout/*.xml` files are legacy View-system leftovers not used by the current Compose navigation graph — don't extend them for new features.

---

## Important Rules & Constraints
* **Do not** add a second Branch session-init path outside `MainActivity.onStart()`/`onNewIntent()` — keep deep link handling centralized there.
* **Do not** wire new features to `kit-libs/` (Twitter/Digits/Fabric) or the `android`/`amazon` source sets without first confirming with the user that reviving those flavors is actually in scope.
* **Always** add new navigation destinations as `Screen` sealed subclasses in `navigation/NavGraph.kt`, not as raw route strings.
* **Always** route new persisted data through a Room `Repository`, not directly through a `Dao`, from ViewModel/Compose code.
* This is a public example/demo repository maintained by Branch (`BranchMetrics` org) — keep changes illustrative and easy to follow for external developers evaluating the Branch SDK, and avoid introducing Branch production secrets or customer data.