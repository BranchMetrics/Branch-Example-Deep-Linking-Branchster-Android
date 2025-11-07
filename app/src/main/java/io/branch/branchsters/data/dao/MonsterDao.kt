package io.branch.branchsters.data.dao

import androidx.room.*
import io.branch.branchsters.data.entity.Monster
import kotlinx.coroutines.flow.Flow

@Dao
interface MonsterDao {
    @Query("SELECT * FROM monsters ORDER BY id ASC")
    fun getAllMonsters(): Flow<List<Monster>>
    
    @Query("SELECT * FROM monsters WHERE id = :monsterId")
    suspend fun getMonsterById(monsterId: Int): Monster?
    
    @Query("SELECT * FROM monsters LIMIT 1")
    fun getCurrentMonster(): Flow<Monster?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonster(monster: Monster)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonsters(monsters: List<Monster>)
    
    @Update
    suspend fun updateMonster(monster: Monster)
    
    @Query("UPDATE monsters SET isOnboarded = :isOnboarded WHERE id = :monsterId")
    suspend fun updateOnboardingStatus(monsterId: Int, isOnboarded: Boolean)
    
    @Query("UPDATE monsters SET branchLink = :branchLink WHERE id = :monsterId")
    suspend fun updateBranchLink(monsterId: Int, branchLink: String)
    
    @Query("UPDATE monsters SET qrCodePath = :qrCodePath WHERE id = :monsterId")
    suspend fun updateQrCodePath(monsterId: Int, qrCodePath: String)
    
    @Query("SELECT * FROM monsters WHERE isOnboarded = 1 LIMIT 1")
    fun getOnboardedMonster(): Flow<Monster?>
    
    @Delete
    suspend fun deleteMonster(monster: Monster)
    
    @Query("DELETE FROM monsters")
    suspend fun deleteAllMonsters()
}
