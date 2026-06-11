package io.branch.branchster.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.branch.branchster.data.entity.BranchEventData
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchEventDao {
    
    @Insert
    suspend fun insertBranchEvent(event: BranchEventData)
    
    @Query("SELECT * FROM branch_events ORDER BY timestamp DESC")
    fun getAllBranchEvents(): Flow<List<BranchEventData>>
    
    @Query("SELECT * FROM branch_events ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestBranchEvent(): BranchEventData?
    
    @Query("DELETE FROM branch_events")
    suspend fun deleteAllBranchEvents()
}
