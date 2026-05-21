package io.branch.branchster.views

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.branch.branchster.ui.theme.imbPlexMonoFamily
import org.json.JSONObject

@Composable
fun DetailsScreen(
    branchData: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val gradientColors = listOf(Color(0xFF2A2D32), Color(0xFF1D1D1D))

    // 1. Parse the incoming branch link metadata
    var monsterColor = ""
    var monsterName = ""
    var monsterLevel = 1

    try {
        if (branchData.isNotBlank()) {
            val json = JSONObject(branchData)
            monsterColor = json.optString("monster_color", "black")
            val rawName = json.optString("monster_name", "Unknown Monster")
            monsterName = rawName
                .replace("+", " ")    // Converts encoded pluses back to spaces
                .trim()              // Cleans up any surrounding white spaces
        }
    } catch (e: Exception) {
        Log.e("DetailsScreen", "Failed to parse Branch link data", e)
    }

    // 2. Dynamically look up the drawable resource for the clicked monster
    val resourceName = "${monsterColor.lowercase()}_monster_level_$monsterLevel"
    val imageRes = try {
        val drawableClass = io.branch.branchster.R.drawable::class.java
        val field = drawableClass.getField(resourceName)
        field.getInt(null)
    } catch (e: Exception) {
        // Fallback placeholder image if resource lookup fails
        io.branch.branchster.R.drawable.onboard_monster_1
    }

    // 3. Render the specific monster details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .clickable { onDismiss() }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = monsterName,
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${monsterColor.uppercase()} $monsterName",
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = imbPlexMonoFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "LEVEL $monsterLevel",
            color = Color(0xffEA2D7F),
            fontSize = 16.sp,
            fontFamily = imbPlexMonoFamily,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tap anywhere to close",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 12.sp,
            fontFamily = imbPlexMonoFamily
        )
    }
}