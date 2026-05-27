package io.branch.branchster.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import io.branch.branchster.ApplicationClass
import io.branch.branchster.R
import io.branch.branchster.ui.theme.imbPlexMonoFamily
import io.branch.branchster.viewmodels.SplashViewModel
import io.branch.branchster.viewmodels.SplashViewModelFactory

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ApplicationClass
    val viewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(application.monsterRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            if (uiState.shouldNavigateToOnboarding) {
                onNavigateToOnboarding()
            } else {
                onNavigateToHome()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF2A2D32), Color(0xFF1D1D1D)),
            ),
        ),
        contentAlignment = Alignment.Center,
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.monster_factory_icon),
                contentDescription = "Monster Factory Icon",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            val textGradient = Brush.verticalGradient(
                colors = listOf(Color(0xFF8A3FFC), Color(0xFF5D04E5)),
            )
            Text(
                text = "Monster Factory",
                style = TextStyle(
                    brush = textGradient,
                    fontSize = 24.sp,
                    fontFamily = imbPlexMonoFamily,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

}