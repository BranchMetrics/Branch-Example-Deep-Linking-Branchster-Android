package io.branch.branchster.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.branch.branchster.data.repository.MonsterRepository
import io.branch.branchster.models.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SplashViewModel(private val monsterRepository: MonsterRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Simulate loading time
            delay(2000)
            
            // Check if user has completed onboarding
            val onboardedMonster = monsterRepository.onboardedMonster.firstOrNull()
            val shouldNavigateToOnboarding = onboardedMonster == null
            
            _uiState.value = SplashUiState(
                isLoading = false,
                shouldNavigateToOnboarding = shouldNavigateToOnboarding
            )
        }
    }
}

class SplashViewModelFactory(private val monsterRepository: MonsterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(monsterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
