package io.branch.branchster.data.dao

import androidx.room.*
import io.branch.branchster.data.entity.Quest
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY id ASC")
    fun getAllQuests(): Flow<List<Quest>>
    
    @Query("SELECT * FROM quests ORDER BY id ASC")
    suspend fun getAllQuestsSync(): List<Quest>
    
    @Query("SELECT * FROM quests WHERE id = :questId")
    suspend fun getQuestById(questId: Int): Quest?
    
    @Query("SELECT * FROM quests WHERE isCompleted = 0 ORDER BY id ASC")
    fun getIncompleteQuests(): Flow<List<Quest>>
    
    @Query("SELECT * FROM quests WHERE isCompleted = 1 ORDER BY id ASC")
    fun getCompletedQuests(): Flow<List<Quest>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<Quest>)
    
    @Update
    suspend fun updateQuest(quest: Quest)
    
    @Query("UPDATE quests SET isCompleted = :isCompleted WHERE id = :questId")
    suspend fun updateQuestCompletion(questId: Int, isCompleted: Boolean)
    
    @Query("UPDATE quests SET isLocked = 0 WHERE dependsOnQuestId = :questId")
    suspend fun unlockDependentQuests(questId: Int)
    
    @Delete
    suspend fun deleteQuest(quest: Quest)
    
    @Query("DELETE FROM quests")
    suspend fun deleteAllQuests()
}
