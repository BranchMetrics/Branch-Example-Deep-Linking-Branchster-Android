package io.branch.branchster.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branch_events")
data class BranchEventData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventName: String,
    val monsterName: String,
    val monsterColor: String,
    val monsterLevel: Int,
    val monsterExp: Int,
    val timestamp: Long = System.currentTimeMillis()
)
