package io.branch.branchsters.views

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.branch.branchsters.ApplicationClass
import io.branch.branchsters.components.BorderedOutlinedTextField
import io.branch.branchsters.ui.theme.Blue
import io.branch.branchsters.ui.theme.ibmPlexMono
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CreateBranchLinkScreen(
    questId: Int = 1,
    onBack: () -> Unit = {}
) {

    val context = LocalContext.current
    val application = context.applicationContext as ApplicationClass
    val monsterRepository = application.monsterRepository
    val questRepository = application.questRepository
    val currentMonster by monsterRepository.currentMonster.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    
    var alias by remember { mutableStateOf("") }
    var generatedLink by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var canonicalIdentifier by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var channel by remember { mutableStateOf("") }
    var feature by remember { mutableStateOf("") }
    var campaign by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf("") }
    var metadataKey by remember { mutableStateOf("") }
    var metadataValue by remember { mutableStateOf("") }

    val selectedChips = remember { mutableStateListOf<String>() }

    val chips = listOf(
        "Canonical ID", "Title", "Description", "Image URL",
        "Channel", "Feature", "Campaign", "Stage", "Metadata"
    )

    val gradientColors = listOf(
        Color(0xFF2A2D32),
        Color(0xFF1D1D1D)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(Color(0xFF2A2D32), Color(0xFF1D1D1D))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
                .padding(all = 20.dp)
        ) {
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Create Branch Link",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = ibmPlexMono,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            BorderedOutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = "Enter Custom Alias (e.g., product/123)",
                fontSize = 12.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Optional Properties:",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = ibmPlexMono,
                fontWeight = FontWeight.Medium
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                chips.forEach { chip ->
                    val isSelected = selectedChips.contains(chip)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedChips.remove(chip) else selectedChips.add(chip)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue,
                            selectedLabelColor = Color.White
                        ),
                        label = {
                            Text(
                                text = chip,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = ibmPlexMono,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if ("Canonical ID" in selectedChips) {
                BorderedOutlinedTextField(
                    value = canonicalIdentifier,
                    onValueChange = { canonicalIdentifier = it },
                    label = "Canonical Identifier",
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
            }

            if ("Title" in selectedChips) {
                BorderedOutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))
            }

            if ("Description" in selectedChips) {
                BorderedOutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))
            }

            if ("Image URL" in selectedChips) {
                BorderedOutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = "Image URL",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))
            }

            if ("Channel" in selectedChips) {
                BorderedOutlinedTextField(
                    value = channel,
                    onValueChange = { channel = it },
                    label = "Channel (e.g., facebook)",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))

                if ("Feature" in selectedChips)
                    BorderedOutlinedTextField(
                        value = feature,
                        onValueChange = { feature = it },
                        label = "Feature (e.g., sharing)",
                        fontSize = 12.sp
                    )

                Spacer(Modifier.height(8.dp))
            }

            if ("Campaign" in selectedChips) {
                BorderedOutlinedTextField(
                    value = campaign,
                    onValueChange = { campaign = it },
                    label = "Campaign (e.g., holiday_sale)",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))
            }
            if ("Stage" in selectedChips) {
                BorderedOutlinedTextField(
                    value = stage,
                    onValueChange = { stage = it },
                    label = "Stage (e.g., new user)",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))
            }

            if ("Metadata" in selectedChips) {
                BorderedOutlinedTextField(
                    value = metadataKey,
                    onValueChange = { metadataKey = it },
                    label = "Metadata Key",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))

                BorderedOutlinedTextField(
                    value = metadataValue,
                    onValueChange = { metadataValue = it },
                    label = "Metadata Value",
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    createDynamicBranchLink(
                        context = context,
                        alias = alias,
                        canonicalId = canonicalIdentifier,
                        title = title,
                        desc = description,
                        imageUrl = imageUrl,
                        channel = channel,
                        feature = feature,
                        campaign = campaign,
                        stage = stage,
                        metaKey = metadataKey,
                        metaValue = metadataValue
                    ) { link ->
                        generatedLink = link
                        isLoading = false

                        // Save the link to the monster's branchLink field
                        link?.let { generatedUrl ->
                            currentMonster?.let { monster ->
                                coroutineScope.launch {
                                    monsterRepository.updateBranchLink(monster.id, generatedUrl)
                                    Log.d(
                                        "CreateBranchLink",
                                        "Saved link to monster: $generatedUrl"
                                    )

                                    // Complete the quest
                                    val quest = questRepository.getQuestById(questId)
                                    quest?.let {
                                        if (!it.isCompleted && !it.isLocked) {
                                            // Mark quest as completed
                                            questRepository.updateQuestCompletion(questId, true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = alias.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.White.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.6f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1E1E1E),
                    contentColor = Color.White,
                    disabledBackgroundColor = Color(0xFF2A2A2A),
                    disabledContentColor = Color.Gray
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (isLoading)
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Blue,
                        strokeWidth = 3.dp
                    )
                else
                    Text(
                        text = "Generate Link",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = ibmPlexMono,
                        fontWeight = FontWeight.Medium
                    )
            }

            Spacer(Modifier.height(24.dp))

            generatedLink?.let {

                val displayLink = it
                    .removePrefix("https://")
                    .removePrefix("http://")

                Text(
                    text = "Generated Link:",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = ibmPlexMono,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectionContainer(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = displayLink,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = ibmPlexMono,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    //
                    //                IconButton(
                    //                    onClick = { shareText(context, it) },
                    //                    modifier = Modifier
                    //                        .size(36.dp)
                    //                        .background(Color.Transparent, RoundedCornerShape(8.dp))
                    //                ) {
                    //                    Icon(
                    //                        imageVector = Icons.Default.Share,
                    //                        contentDescription = "Share Link",
                    //                        tint = Color.White
                    //                    )
                    //                }
                }
            }

        }
    }
}

fun createDynamicBranchLink(
    context: Context,
    alias: String,
    canonicalId: String?,
    title: String?,
    desc: String?,
    imageUrl: String?,
    channel: String?,
    feature: String?,
    campaign: String?,
    stage: String?,
    metaKey: String?,
    metaValue: String?,
    callback: (String?) -> Unit
) {
    val buo = BranchUniversalObject()
        .setCanonicalIdentifier(canonicalId ?: "content/${System.currentTimeMillis()}")
        .setTitle(title ?: "Default Title")
        .setContentDescription(desc ?: "No description provided.")
        .setContentImageUrl(imageUrl ?: "")
        .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
        .setContentMetadata(
            ContentMetadata().apply {
                if (!metaKey.isNullOrBlank() && !metaValue.isNullOrBlank()) {
                    addCustomMetadata(metaKey, metaValue)
                }
            }
        )

    val lp = LinkProperties()
        .setAlias(alias)
        .apply {
            if (!channel.isNullOrBlank()) setChannel(channel)
            if (!feature.isNullOrBlank()) setFeature(feature)
            if (!campaign.isNullOrBlank()) setCampaign(campaign)
            if (!stage.isNullOrBlank()) setStage(stage)
        }

    buo.generateShortUrl(context, lp) { url, error ->
        if (error == null) {
            Log.d("BranchSDK", "Generated Link: $url")
            callback(url)
        } else {
            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }
}

fun shareText(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Branch Link"))
}

private fun calculateLevel(completedQuests: Int): Int {
    return when {
        completedQuests >= 6 -> 4
        completedQuests >= 4 -> 3
        completedQuests >= 2 -> 2
        else -> 1
    }
}

private fun getMonsterImageForLevel(monsterName: String, level: Int): Int {
    // Extract color from monsterName (e.g., "black_monster_level_1" -> "black")
    // Format: {color}_monster_level_{level}
    val colorPrefix = monsterName.substringBefore("_monster_level_")
    val resourceName = "${colorPrefix}_monster_level_${level}"

    return try {
        val context = io.branch.branchsters.R.drawable::class.java
        val field = context.getField(resourceName)
        field.getInt(null)
    } catch (e: Exception) {
        // Fallback to default image if resource not found
        io.branch.branchsters.R.drawable.onboard_monster_1
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CreateLinkScreenPreview() {
    CreateBranchLinkScreen()
}