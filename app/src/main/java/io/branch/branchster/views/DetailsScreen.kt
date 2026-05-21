package io.branch.branchster.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.branch.branchster.ApplicationClass
import io.branch.branchster.ui.theme.imbPlexMonoFamily
import io.branch.branchster.viewmodels.HomeViewModel
import io.branch.branchster.viewmodels.HomeViewModelFactory

@Composable
fun DetailsScreen(
    branchData: String,
    onDismiss: () -> Unit
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
    val gradientColors = listOf(
        Color(0xFF2A2D32),
        Color(0xFF1D1D1D)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
            .clickable { onDismiss() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        uiState.currentMonster?.let { monster ->
            Image(
                painter = painterResource(monster.monsterImage),
                contentDescription = monster.monsterName,
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = monster.monsterTitle,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = imbPlexMonoFamily,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap anywhere to close",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontFamily = imbPlexMonoFamily
            )
        }
    }
}