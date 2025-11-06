package io.branch.branchsters.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monsters")
data class Monster(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monsterName: String,
    val monsterTitle: String, // Monster's title or role
    val monsterImage: Int, // URL or resource path for the monster image
    val isOnboarded: Boolean = false, // Tracks if user has completed onboarding
    val level: Int = 1, // Monster's current level
    val xp: Int = 0 // Monster's current XP
)
