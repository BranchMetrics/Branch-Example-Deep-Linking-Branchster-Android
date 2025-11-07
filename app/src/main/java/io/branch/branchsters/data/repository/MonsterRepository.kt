package io.branch.branchsters.data.repository

import io.branch.branchsters.data.dao.MonsterDao
import io.branch.branchsters.data.entity.Monster
import kotlinx.coroutines.flow.Flow

class MonsterRepository(private val monsterDao: MonsterDao) {
    
    val allMonsters: Flow<List<Monster>> = monsterDao.getAllMonsters()
    
    val currentMonster: Flow<Monster?> = monsterDao.getCurrentMonster()
    
    val onboardedMonster: Flow<Monster?> = monsterDao.getOnboardedMonster()
    
    suspend fun getMonsterById(monsterId: Int): Monster? {
        return monsterDao.getMonsterById(monsterId)
    }
    
    suspend fun updateOnboardingStatus(monsterId: Int, isOnboarded: Boolean) {
        monsterDao.updateOnboardingStatus(monsterId, isOnboarded)
    }
    
    suspend fun updateBranchLink(monsterId: Int, branchLink: String) {
        monsterDao.updateBranchLink(monsterId, branchLink)
    }
    
    suspend fun updateQrCodePath(monsterId: Int, qrCodePath: String) {
        monsterDao.updateQrCodePath(monsterId, qrCodePath)
    }
    
    suspend fun insertMonster(monster: Monster) {
        monsterDao.insertMonster(monster)
    }
    
    suspend fun insertMonsters(monsters: List<Monster>) {
        monsterDao.insertMonsters(monsters)
    }
    
    suspend fun updateMonster(monster: Monster) {
        monsterDao.updateMonster(monster)
    }
    
    suspend fun deleteMonster(monster: Monster) {
        monsterDao.deleteMonster(monster)
    }
    
    suspend fun deleteAllMonsters() {
        monsterDao.deleteAllMonsters()
    }
}
