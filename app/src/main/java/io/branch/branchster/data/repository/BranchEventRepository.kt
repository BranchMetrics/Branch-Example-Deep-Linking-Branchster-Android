package io.branch.branchster.data.repository

import io.branch.branchster.data.dao.BranchEventDao
import io.branch.branchster.data.entity.BranchEventData
import kotlinx.coroutines.flow.Flow

class BranchEventRepository(private val branchEventDao: BranchEventDao) {
    
    val allBranchEvents: Flow<List<BranchEventData>> = branchEventDao.getAllBranchEvents()
    
    suspend fun insertBranchEvent(event: BranchEventData) {
        branchEventDao.insertBranchEvent(event)
    }
    
    suspend fun getLatestBranchEvent(): BranchEventData? {
        return branchEventDao.getLatestBranchEvent()
    }
    
    suspend fun deleteAllBranchEvents() {
        branchEventDao.deleteAllBranchEvents()
    }
}
