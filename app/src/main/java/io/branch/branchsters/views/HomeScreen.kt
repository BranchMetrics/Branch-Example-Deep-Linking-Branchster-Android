package io.branch.branchsters.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.branch.branchsters.ApplicationClass
import io.branch.branchsters.R
import io.branch.branchsters.data.entity.Quest
import io.branch.branchsters.ui.theme.ibmPlexMono
import io.branch.branchsters.viewmodels.HomeViewModel
import io.branch.branchsters.viewmodels.HomeViewModelFactory

@Composable
fun HomeScreen(
    onNavigateToGemini: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as ApplicationClass
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            application.questRepository,
            application.monsterRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2A2D32), Color(0xFF1D1D1D)),
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding() // Replaces SafeArea
                    .padding(horizontal = 18.dp)
            ) {

                Spacer(Modifier.height(10.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.currentMonster!!.monsterTitle,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = ibmPlexMono,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border( // Replaces border: Border.all()
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .clip(RoundedCornerShape(5.dp)) // Ensures background respects the border radius
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFFFFF).copy(alpha = 0.1f),
                                    Color(0xFFFFFFFF).copy(alpha = 0.05f)
                                )
                            )
                        ) // Replaces color: Colors.white10
                        .padding(18.dp), // Replaces padding: EdgeInsets.all(20)
                    horizontalAlignment = Alignment.CenterHorizontally // To center the image
                ) {

                    // Display monster image with animation
                    val currentMonsterImage = uiState.currentMonster?.monsterImage ?: R.drawable.monster_truck
                    val currentLevel = uiState.currentMonster?.level ?: 1
                    
                    // Track level changes for animation trigger
                    var previousLevel by remember { mutableStateOf(currentLevel) }
                    var imageScale by remember { mutableStateOf(1f) }
                    
                    // Trigger scale animation on level change
                    LaunchedEffect(currentLevel) {
                        if (currentLevel != previousLevel) {
                            // Scale up animation
                            imageScale = 1.2f
                            kotlinx.coroutines.delay(300)
                            imageScale = 1f
                            previousLevel = currentLevel
                        }
                    }
                    
                    // Animated scale
                    val animatedScale by animateFloatAsState(
                        targetValue = imageScale,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "image_scale"
                    )
                    
                    // Crossfade animation for image transition
                    Crossfade(
                        targetState = currentMonsterImage,
                        animationSpec = tween(durationMillis = 1500),
                        label = "monster_image_crossfade"
                    ) { imageRes ->
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = uiState.currentMonster?.monsterName ?: "Monster",
                            modifier = Modifier
                                .height(250.dp)
                                .graphicsLayer(
                                    scaleX = animatedScale,
                                    scaleY = animatedScale
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // This Column replaces Align + Column
                    Column(
                        modifier = Modifier.fillMaxWidth() // Ensures children fill the width
                    ) {
                        Spacer(Modifier.height(8.dp))
                        
                        // Calculate dynamic values
                        val currentLevel = uiState.currentMonster?.level ?: 1
                        val currentXp = uiState.currentMonster?.xp ?: 0
                        val totalXp = 300 // Total XP for all 6 quests (6 × 50 = 300)
                        val xpProgress = currentXp.toFloat() / totalXp.toFloat()
                        
                        // Animated XP value
                        var targetXp by remember { mutableStateOf(currentXp) }
                        LaunchedEffect(currentXp) {
                            targetXp = currentXp
                        }
                        val animatedXp by animateIntAsState(
                            targetValue = targetXp,
                            animationSpec = tween(durationMillis = 1000),
                            label = "xp_animation"
                        )
                        
                        // Animated progress
                        val animatedProgress by animateFloatAsState(
                            targetValue = xpProgress,
                            animationSpec = tween(durationMillis = 1000),
                            label = "progress_animation"
                        )
                        
                        // XP Progress Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "LEVEL $currentLevel",
                                style = TextStyle(
                                    fontFamily = ibmPlexMono,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )

                            Spacer(Modifier.weight(1f))

                            Text(
                                text = "XP $animatedXp/$totalXp",
                                style = TextStyle(
                                    fontFamily = ibmPlexMono,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xffEA2D7F),
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Challenges Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CHALLENGES",
                        style = TextStyle(
                            fontFamily = ibmPlexMono,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val completedCount = uiState.quests.count { it.isCompleted }
                        val totalCount = uiState.quests.size
                        
                        Text(
                            text = "$completedCount/$totalCount",
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Text(
                            text = "completed",
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Quest List
                if (uiState.quests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No quests available",
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        )
                    }
                } else {

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        uiState.quests.forEach { quest ->
                            QuestItem(
                                quest = quest,
                                onToggleCompletion = {
                                    viewModel.toggleQuestCompletion(quest.id)
                                }
                            )
                        }
                    }

                }
                Spacer(Modifier.height(16.dp))
            }


        }
    }
}

@Composable
fun QuestItem(
    quest: Quest,
    onToggleCompletion: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(5.dp)
            )
            .clip(RoundedCornerShape(5.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFFFF).copy(alpha = 0.1f),
                        Color(0xFFFFFFFF).copy(alpha = 0.05f)
                    )
                )
            )
            .then(
                if (!quest.isCompleted) {
                    Modifier.clickable { onToggleCompletion() }
                } else {
                    Modifier
                }
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Check icon on the left
            Icon(
                imageVector = if (quest.isCompleted)
                    Icons.Filled.CheckCircle
                else
                    Icons.Outlined.CheckCircle,
                contentDescription = if (quest.isCompleted) "Completed" else "Incomplete",
                tint = if (quest.isCompleted)
                    Color.Gray
                else
                    Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Quest text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.name,
                    style = TextStyle(
                        fontFamily = ibmPlexMono,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textDecoration = if (quest.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = quest.description,
                    style = TextStyle(
                        fontFamily = ibmPlexMono,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    ),
                    textDecoration = if (quest.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Lock icon on the right (only show if locked)
            if (quest.isLocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
