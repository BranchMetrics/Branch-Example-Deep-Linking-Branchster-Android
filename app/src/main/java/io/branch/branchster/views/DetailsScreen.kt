package io.branch.branchster.views

import androidx.compose.foundation.Image
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.branch.branchster.ApplicationClass
import io.branch.branchster.viewmodels.HomeViewModel
import io.branch.branchster.viewmodels.HomeViewModelFactory

@Composable
fun DetailsScreen(
    branchData: String
) {
    val context = LocalContext.current
    val application = context.applicationContext as ApplicationClass
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            application.questRepository,
            application.monsterRepository,
            application.branchEventRepository,
            application.soundManager
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    // Use branchData (log or decode)
    Text(text = "Branch Data: $branchData")

    uiState.currentMonster?.let {
        Image(
            painter = painterResource(it.monsterImage),
            contentDescription = null
        )
        Text(text = it.monsterName)
    }
}
