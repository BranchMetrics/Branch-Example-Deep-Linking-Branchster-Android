package io.branch.branchsters.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.branch.branchsters.data.entity.Quest
import io.branch.branchsters.data.repository.MonsterRepository
import io.branch.branchsters.data.repository.QuestRepository
import io.branch.branchsters.models.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val questRepository: QuestRepository,
    private val monsterRepository: MonsterRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                monsterRepository.currentMonster,
                questRepository.allQuests
            ) { monster, quests ->
                _uiState.value.copy(
                    currentMonster = monster,
                    quests = quests,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun toggleQuestCompletion(questId: Int) {
        viewModelScope.launch {
            val quest = questRepository.getQuestById(questId)
            quest?.let {
                questRepository.updateQuestCompletion(questId, !it.isCompleted)
            }
        }
    }

    fun addQuest(quest: Quest) {
        viewModelScope.launch {
            questRepository.insertQuest(quest)
        }
    }
}

class HomeViewModelFactory(
    private val questRepository: QuestRepository,
    private val monsterRepository: MonsterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(questRepository, monsterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
