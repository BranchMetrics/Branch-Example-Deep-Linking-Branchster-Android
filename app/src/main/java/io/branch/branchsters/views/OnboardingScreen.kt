package io.branch.branchsters.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import io.branch.branchsters.ApplicationClass
import io.branch.branchsters.R
import io.branch.branchsters.viewmodels.OnboardingViewModel
import io.branch.branchsters.viewmodels.OnboardingViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class OnboardingPage {
    abstract val imageRes: Int
    abstract val title: String
    abstract val description: String
    
    data class Basic(
        override val imageRes: Int,
        override val title: String,
        override val description: String
    ) : OnboardingPage()

    data class WithList(
        override val imageRes: Int,
        override val title: String,
        override val description: String,
        val items: List<ListItem>
    ) : OnboardingPage()
}

data class ListItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

// Theme constants
private object OnboardingTheme {
    val BackgroundGradient = listOf(Color(0xFF2A2D32), Color(0xFF1D1D1D))
    val CardGradient = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.1f),
        Color(0xFFFFFFFF).copy(alpha = 0.05f)
    )
    val ImageGradient = listOf(Color(0xFF2FB8FF), Color(0xFF9EECD9))
    val BorderColor = Color.White
    val TextColor = Color.White
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as ApplicationClass
    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(
            application.monsterRepository,
            application.questRepository
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val pages = listOf(
        OnboardingPage.Basic(
            title = "See what Branch\ncan do for you",
            description = "Complete challenges to\nlearn about Branch\nfunctionality and\ncapabilities.",
            imageRes = R.drawable.qr_code
        ),
        OnboardingPage.Basic(
            title = "Customize your\nmonster",
            description = "Earn XP to unlock ways to\ncustomize your monster\nby completing challenges.",
            imageRes = R.drawable.onboard_monster_1
        ),
        OnboardingPage.WithList(
            title = "Select your\nmonster",
            description = "Share your monsters with friends and the community",
            imageRes = R.drawable.onboard_monster_2,
            items = listOf(
                ListItem(
                    title = "Black - Grave Whisper",
                    description = "A being that speaks through the mouths of the dead; its voice sounds like distant thunder under the ground.",
                    imageRes = R.drawable.black_monster_level_1
                ),
                ListItem(
                    title = "Blue - Abyss Caller",
                    description = "Summons echoes from the deep ocean — voices that twist sailors’ minds into madness.",
                    imageRes = R.drawable.blue_monster_level_1
                ),
                ListItem(
                    title = "Green - Rot Weaver",
                    description = "Spins fungal threads through soil and corpses, knitting decay into new lifeforms.",
                    imageRes = R.drawable.green_monster_level_1
                ),
                ListItem(
                    title = "Orange - Cinder Howl",
                    description = "A molten beast whose roar ignites the air; its breath smells like burning stone.",
                    imageRes = R.drawable.orange_monster_level_1
                ),
                ListItem(
                    title = "Pink - Bliss Warden",
                    description = "Guards the border between pleasure and pain — its smile makes mortals forget which side they’re on.",
                    imageRes = R.drawable.pink_monster_level_1
                ),
                ListItem(
                    title = "Purple – Mind Shiver",
                    description = "SA psychic predator that freezes thoughts mid-sentence; its presence feels like déjà vu and static.",
                    imageRes = R.drawable.purple_monster_level_1
                ),
                ListItem(
                    title = "Red – Rage Bastion",
                    description = "A fortress-shaped creature fueled by fury; every wound it takes turns into armor.",
                    imageRes = R.drawable.red_monster_level_1
                ),
                ListItem(
                    title = "White – Halo Scorn",
                    description = "An angelic horror with a broken crown of light; it hunts purity in the name of perfection.",
                    imageRes = R.drawable.white_monster_level_1
                ),
                ListItem(
                    title = "Yellow – Dusk Gleam",
                    description = "A radiant trickster that appears at sunset; those who chase its glow vanish into mirages.",
                    imageRes = R.drawable.yellow_monster_level_1
                ),
            )
        )
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(OnboardingTheme.BackgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Horizontal Pager
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val page = pages[pageIndex]
                when (page) {
                    is OnboardingPage.Basic -> OnboardingPageContent(
                        page = page,
                        pagerState = pagerState,
                        pagesCount = pages.size,
                        coroutineScope = coroutineScope,
                        onFinish = onFinish
                    )
                    is OnboardingPage.WithList -> OnboardingPageContent(
                        page = page,
                        pagerState = pagerState,
                        pagesCount = pages.size,
                        coroutineScope = coroutineScope,
                        onFinish = { viewModel.completeOnboarding(onFinish) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Carousel Indicators
            CarouselIndicators(
                pagerState = pagerState,
                pageCount = pages.size
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pagerState: PagerState,
    pagesCount: Int,
    coroutineScope: CoroutineScope,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        OnboardingImageCard(imageRes = page.imageRes, contentDescription = page.title)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OnboardingTitle(text = page.title)
        
        Spacer(modifier = Modifier.height(25.dp))
        
        when (page) {
            is OnboardingPage.Basic -> {
                OnboardingDescription(text = page.description)
                Spacer(modifier = Modifier.height(35.dp))
                ArrowButton(
                    pagerState = pagerState,
                    pagesCount = pagesCount,
                    coroutineScope = coroutineScope,
                    onFinish = onFinish
                )
            }
            is OnboardingPage.WithList -> {
                MonsterSelectionList(
                    items = page.items,
                    pageImageRes = page.imageRes,
                    onFinish = onFinish
                )
            }
        }
    }
}

@Composable
private fun OnboardingImageCard(
    imageRes: Int,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(325.dp)
            .background(
                brush = Brush.verticalGradient(OnboardingTheme.CardGradient),
                shape = RoundedCornerShape(5.dp)
            )
            .border(0.5.dp, OnboardingTheme.BorderColor, shape = RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(
                    brush = Brush.verticalGradient(OnboardingTheme.ImageGradient),
                    shape = RoundedCornerShape(1.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(175.dp)
            )
        }
    }
}

@Composable
private fun OnboardingTitle(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = OnboardingTheme.TextColor,
        textAlign = TextAlign.Start,
        lineHeight = 50.sp
    )
}

@Composable
private fun OnboardingDescription(text: String) {
    Text(
        text = text,
        fontSize = 24.sp,
        color = OnboardingTheme.TextColor,
        textAlign = TextAlign.Start,
        lineHeight = 40.sp
    )
}

@Composable
private fun MonsterSelectionList(
    items: List<ListItem>,
    pageImageRes: Int,
    onFinish: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items.forEach { item ->
            MonsterListItem(
                item = item,
                imageRes = item.imageRes,
                onClick = onFinish
            )
        }
    }
}

@Composable
private fun MonsterListItem(
    item: ListItem,
    imageRes: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(
                brush = Brush.verticalGradient(OnboardingTheme.CardGradient),
                shape = RoundedCornerShape(5.dp)
            )
            .clickable(onClick = onClick)
            .border(0.5.dp, OnboardingTheme.BorderColor, shape = RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = item.title,
                modifier = Modifier.size(70.dp)
            )
            Spacer(Modifier.width(15.dp))
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = OnboardingTheme.TextColor,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                lineHeight = 40.sp
            )
        }
    }
}

@Composable
private fun CarouselIndicators(
    pagerState: PagerState,
    pageCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            val colors = if (isSelected) {
                listOf(Color.White, Color.White)
            } else {
                OnboardingTheme.BackgroundGradient
            }
            
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.verticalGradient(colors))
                    .border(
                        width = 1.dp,
                        color = OnboardingTheme.BorderColor,
                        shape = CircleShape
                    )
            )
            
            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


@Composable
private fun ArrowButton(
    pagerState: PagerState,
    pagesCount: Int,
    coroutineScope: CoroutineScope,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(1.dp, OnboardingTheme.BorderColor, shape = RoundedCornerShape(5.dp))
            .clickable {
                if (pagerState.currentPage < pagesCount - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinish()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = "Next",
            tint = OnboardingTheme.TextColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
