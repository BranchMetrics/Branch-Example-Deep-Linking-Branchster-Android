package io.branch.branchster.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.branch.branchster.data.entity.Monster
import io.branch.branchster.data.repository.MonsterRepository
import io.branch.branchster.data.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    var currentMonster: Monster? = null,
    val isOnboardingComplete: Boolean = false
)

class OnboardingViewModel(
    private val monsterRepository: MonsterRepository,
    private val questRepository: QuestRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadCurrentMonster()
    }

    private fun loadCurrentMonster() {
        viewModelScope.launch {
            monsterRepository.currentMonster.collect { monster ->
                _uiState.value = _uiState.value.copy(
                    currentMonster = monster,
                )
            }
        }
    }

    fun completeOnboarding(onComplete: () -> Unit, title: String, name: String, image: Int) {
        viewModelScope.launch {
            val monster = _uiState.value.currentMonster

                // Update existing monster
                val updatedMonster = monster?.copy(
                    monsterName = name,
                    monsterTitle = title,
                    monsterImage = image,
                    isOnboarded = true
                )
                monsterRepository.updateMonster(updatedMonster!!)

            _uiState.value = _uiState.value.copy(isOnboardingComplete = true)
            onComplete()
        }
    }
}

class OnboardingViewModelFactory(
    private val monsterRepository: MonsterRepository,
    private val questRepository: QuestRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(monsterRepository, questRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
