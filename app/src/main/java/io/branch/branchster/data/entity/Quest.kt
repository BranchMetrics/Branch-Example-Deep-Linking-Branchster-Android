package io.branch.branchster.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true,)
    val id: Int = 0,
    val name: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
    val dependsOnQuestId: Int? = null, // ID of the quest this quest depends on
    val icon: Int = 0 // Drawable resource ID for the quest icon
)
