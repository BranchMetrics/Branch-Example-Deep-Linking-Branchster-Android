package io.branch.branchster.models

import android.graphics.Bitmap
import io.branch.branchster.data.entity.Monster
import io.branch.branchster.data.entity.Quest

data class HomeUiState(
    val currentMonster: Monster? = null,
    val quests: List<Quest> = emptyList(),
    val isLoading: Boolean = true,
    val branchLink:  String = "",
    val showOverlay: Boolean = false,
    val showTriggerEventOverlay: Boolean = false,
    val showQrCodeOverlay: Boolean = false,
    val qrCodeImage: Bitmap? = null,
    val showViewBranchDataOverlay: Boolean = false,
)
