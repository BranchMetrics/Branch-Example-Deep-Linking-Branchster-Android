package io.branch.branchsters.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monsters")
data class Monster(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monsterName: String,
    val monsterImage: String, // URL or resource path for the monster image
    val isOnboarded: Boolean = false // Tracks if user has completed onboarding
)
