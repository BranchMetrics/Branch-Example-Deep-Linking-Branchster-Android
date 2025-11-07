package io.branch.branchsters.data.repository

import io.branch.branchsters.data.dao.QuestDao
import io.branch.branchsters.data.entity.Quest
import kotlinx.coroutines.flow.Flow

class QuestRepository(private val questDao: QuestDao) {
    
    val allQuests: Flow<List<Quest>> = questDao.getAllQuests()
    
    val incompleteQuests: Flow<List<Quest>> = questDao.getIncompleteQuests()
    
    val completedQuests: Flow<List<Quest>> = questDao.getCompletedQuests()
    
    suspend fun getAllQuestsSync(): List<Quest> {
        return questDao.getAllQuestsSync()
    }
    
    suspend fun getQuestById(questId: Int): Quest? {
        return questDao.getQuestById(questId)
    }
    
    suspend fun insertQuest(quest: Quest) {
        questDao.insertQuest(quest)
    }
    
    suspend fun insertQuests(quests: List<Quest>) {
        questDao.insertQuests(quests)
    }
    
    suspend fun updateQuest(quest: Quest) {
        questDao.updateQuest(quest)
    }
    
    suspend fun updateQuestCompletion(questId: Int, isCompleted: Boolean) {
        questDao.updateQuestCompletion(questId, isCompleted)
    }
    
    suspend fun unlockDependentQuests(questId: Int) {
        questDao.unlockDependentQuests(questId)
    }
    
    suspend fun deleteQuest(quest: Quest) {
        questDao.deleteQuest(quest)
    }
    
    suspend fun deleteAllQuests() {
        questDao.deleteAllQuests()
    }
}
