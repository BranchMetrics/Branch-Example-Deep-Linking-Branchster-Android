package io.branch.branchsters.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.branch.branchsters.data.entity.Monster
import io.branch.branchsters.data.repository.MonsterRepository
import io.branch.branchsters.data.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentMonster: Monster? = null,
    val selectedMonsterName: String = "",
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
                    selectedMonsterName = monster?.monsterName ?: ""
                )
            }
        }
    }

    fun selectMonsterName(name: String) {
        _uiState.value = _uiState.value.copy(selectedMonsterName = name)
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val monster = _uiState.value.currentMonster
            
            if (monster != null) {
                // Update existing monster
                val updatedMonster = monster.copy(
                    monsterName = _uiState.value.selectedMonsterName.ifEmpty { monster.monsterName },
                    isOnboarded = true
                )
                monsterRepository.updateMonster(updatedMonster)
            } else {
                // Create new monster if none exists
                val newMonster = Monster(
                    monsterName = _uiState.value.selectedMonsterName.ifEmpty { "Starter Branchster" },
                    monsterImage = "default_monster",
                    isOnboarded = true
                )
                monsterRepository.insertMonster(newMonster)
            }
            
            // Mark first quest as completed (onboarding quest)
            val firstQuest = questRepository.allQuests.firstOrNull()?.firstOrNull()
            firstQuest?.let {
                questRepository.updateQuestCompletion(it.id, true)
            }
            
            _uiState.value = _uiState.value.copy(isOnboardingComplete = true)
            onComplete()
        }
    }

    fun updateMonsterOnboardingStatus(monsterId: Int, isOnboarded: Boolean) {
        viewModelScope.launch {
            monsterRepository.updateOnboardingStatus(monsterId, isOnboarded)
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
