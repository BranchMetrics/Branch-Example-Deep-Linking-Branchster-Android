package io.branch.branchsters.models

import io.branch.branchsters.data.entity.Monster
import io.branch.branchsters.data.entity.Quest

data class HomeUiState(
    val welcomeMessage: String = "Welcome to Branchsters!",
    val description: String = "Your monster factory adventure starts here",
    val currentMonster: Monster? = null,
    val quests: List<Quest> = emptyList(),
    val isLoading: Boolean = true
)
