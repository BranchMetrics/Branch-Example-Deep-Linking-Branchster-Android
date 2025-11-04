# MVVM Architecture Documentation

This project follows a clean MVVM (Model-View-ViewModel) architecture pattern with proper separation of concerns.

## Architecture Layers

### 📦 Models (`models/`)
Contains data classes and business logic models that represent the application's data structure.

**Files:**
- `HomeUiState.kt` - Data model for Home screen UI state
- `SplashUiState.kt` - Data model for Splash screen UI state

**Responsibilities:**
- Define data structures
- Represent UI state
- Hold business logic entities
- No Android framework dependencies

### 👁️ Views (`views/`)
Contains Composable UI functions that render the user interface.

**Files:**
- `SplashScreen.kt` - Splash screen UI implementation
- `HomeScreen.kt` - Home screen UI implementation

**Responsibilities:**
- Render UI using Jetpack Compose
- Observe ViewModel state
- Handle user interactions
- Delegate business logic to ViewModels
- No business logic

### 🎮 ViewModels (`viewmodels/`)
Contains ViewModel classes that manage UI-related data and business logic.

**Files:**
- `SplashViewModel.kt` - Manages Splash screen state and logic
- `HomeViewModel.kt` - Manages Home screen state and logic

**Responsibilities:**
- Manage UI state using StateFlow
- Handle business logic
- Survive configuration changes
- Expose data to Views
- No direct UI references

## Data Flow

```
User Interaction → View → ViewModel → Model
                    ↑         ↓
                    └─────────┘
                   (StateFlow)
```

1. **User interacts** with the View (Composable)
2. **View calls** ViewModel methods
3. **ViewModel updates** Model/State
4. **StateFlow emits** new state
5. **View observes** and recomposes with new state

## Package Structure

```
io.branch.branchsters/
├── models/                    # Data models and UI states
│   ├── HomeUiState.kt
│   └── SplashUiState.kt
├── viewmodels/               # ViewModels for business logic
│   ├── HomeViewModel.kt
│   └── SplashViewModel.kt
├── views/                    # Composable UI screens
│   ├── HomeScreen.kt
│   └── SplashScreen.kt
├── navigation/               # Navigation graph
│   └── NavGraph.kt
├── ui/theme/                 # Theme and styling
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── MainActivity.kt           # App entry point
```

## Benefits of This Architecture

1. **Separation of Concerns**: Each layer has a single, well-defined responsibility
2. **Testability**: ViewModels can be unit tested without Android framework
3. **Maintainability**: Changes in one layer don't affect others
4. **Scalability**: Easy to add new features following the same pattern
5. **Lifecycle Awareness**: ViewModels survive configuration changes
6. **Reactive UI**: StateFlow ensures UI is always in sync with data

## Adding New Features

### 1. Create a Model
```kotlin
// models/NewFeatureUiState.kt
data class NewFeatureUiState(
    val data: String = ""
)
```

### 2. Create a ViewModel
```kotlin
// viewmodels/NewFeatureViewModel.kt
class NewFeatureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewFeatureUiState())
    val uiState: StateFlow<NewFeatureUiState> = _uiState.asStateFlow()
}
```

### 3. Create a View
```kotlin
// views/NewFeatureScreen.kt
@Composable
fun NewFeatureScreen(viewModel: NewFeatureViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    // UI implementation
}
```

### 4. Add Navigation
Update `navigation/NavGraph.kt` to include the new screen route.

## Best Practices

- ✅ Keep Views dumb - they should only render UI
- ✅ Keep ViewModels free of Android framework dependencies
- ✅ Use StateFlow for reactive state management
- ✅ Models should be immutable data classes
- ✅ Use dependency injection for ViewModels (when needed)
- ✅ Handle configuration changes in ViewModels
- ❌ Don't put business logic in Views
- ❌ Don't hold View references in ViewModels
- ❌ Don't make direct API calls from Views
