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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val questRepository: QuestRepository,
    private val monsterRepository: MonsterRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var currentQuestID = 0;

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
            val currentMonster = _uiState.value.currentMonster
            
            quest?.let {
                if (!it.isCompleted && !it.isLocked) {
                    // Mark quest as completed
                    questRepository.updateQuestCompletion(questId, true)
                    
                    // Unlock any quests that depend on this quest
                    questRepository.unlockDependentQuests(questId)
                    
                    // Add XP and update level
                    currentMonster?.let { monster ->
                        val newXp = monster.xp + 50 // Each quest = 50 XP
                        val completedQuests = _uiState.value.quests.count { q -> q.isCompleted } + 1
                        val newLevel = calculateLevel(completedQuests)
                        val newImage = getMonsterImageForLevel(monster.monsterName, newLevel)
                        
                        val updatedMonster = monster.copy(
                            xp = newXp,
                            level = newLevel,
                            monsterImage = newImage
                        )
                        monsterRepository.updateMonster(updatedMonster)
                        currentQuestID = 0;
                    }
                }
            }
        }
    }


    fun refreshProgress(questId: Int) {
        viewModelScope.launch {
            val quest = questRepository.getQuestById(questId)
            val currentMonster = _uiState.value.currentMonster

            quest?.let {
                if (it.isCompleted) {
                    // Unlock any quests that depend on this quest
                    questRepository.unlockDependentQuests(questId)

                    // Add XP and update level
                    currentMonster?.let { monster ->
                        val newXp = monster.xp + 50 // Each quest = 50 XP
                        val completedQuests = _uiState.value.quests.count { q -> q.isCompleted }
                        val newLevel = calculateLevel(completedQuests)
                        val newImage = getMonsterImageForLevel(monster.monsterName, newLevel)

                        val updatedMonster = monster.copy(
                            xp = newXp,
                            level = newLevel,
                            monsterImage = newImage
                        )
                        monsterRepository.updateMonster(updatedMonster)
                        currentQuestID = 0;
                    }
                }
            }
        }
    }

    fun openOverlayForLinkShare(questId: Int){
        currentQuestID = questId
        val branchLink = _uiState.value.currentMonster?.branchLink
        val shouldShowOverlay = !branchLink.isNullOrEmpty() && (_uiState.value.quests.find { it.id == questId }?.isCompleted == false)
       _uiState.update { it.copy(showOverlay = shouldShowOverlay, branchLink = branchLink ?: "") }
    }
    
    fun onLinkShared() {
        viewModelScope.launch {
            _uiState.update { it.copy(showOverlay = false) }
            toggleQuestCompletion(currentQuestID)
        }
    }


    
    private fun calculateLevel(completedQuests: Int): Int {
        return when {
            completedQuests >= 6 -> 4
            completedQuests >= 4 -> 3
            completedQuests >= 2 -> 2
            else -> 1
        }
    }
    
    private fun getMonsterImageForLevel(monsterName: String, level: Int): Int {
        // Extract color from monsterName (e.g., "black_monster_level_1" -> "black")
        // Format: {color}_monster_level_{level}
        val colorPrefix = monsterName.substringBefore("_monster_level_")
        val resourceName = "${colorPrefix}_monster_level_${level}"
        
        return try {
            val context = io.branch.branchsters.R.drawable::class.java
            val field = context.getField(resourceName)
            field.getInt(null)
        } catch (e: Exception) {
            // Fallback to current image if resource not found
            _uiState.value.currentMonster?.monsterImage ?: io.branch.branchsters.R.drawable.onboard_monster_1
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
