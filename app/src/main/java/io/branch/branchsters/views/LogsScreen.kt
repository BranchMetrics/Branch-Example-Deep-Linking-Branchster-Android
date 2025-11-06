package io.branch.branchsters.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.branch.branchsters.ui.theme.ibmPlexMono
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun LogsScreen(branchData: String) {

    val gradientColors = listOf(
        Color(0xFF2A2D32),
        Color(0xFF1D1D1D)
    )

    val scrollState = rememberScrollState()

    val formattedData = remember(branchData) {
        try {
            if (branchData.trim().startsWith("[")) {
                JSONArray(branchData).toString(4)
            } else {
                JSONObject(branchData).toString(4)
            }
        } catch (e: Exception) {
            branchData.ifEmpty { "No Branch data available." }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Branch Logs",
            color = Color.White,
            fontSize = 22.sp,
            fontFamily = ibmPlexMono,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = Color(0x6F131313),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            SelectionContainer {
                Text(
                    text = formattedData,
                    color = Color(0xFFEEEEEE),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = ibmPlexMono,
                    fontWeight = FontWeight.Normal
                )
            }
        }

//        Text(
//            text = branchData.ifEmpty { "No Branch data available." },
//            fontFamily = imbPlexMonoFamily,
//            fontWeight = FontWeight.SemiBold,
//            color = Color(0xFFCCCCCC),
//            fontSize = 14.sp,
//            lineHeight = 20.sp
//        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1D1D1D)
@Composable
fun LogsScreenPreview() {
    val sampleData = """
        {
            "clicked_branch_link": true,
            "deep_link_value": "product/1234",
            "campaign": "holiday_sale",
            "user_data": {
                "user_id": "abc123",
                "referrer": "ad_campaign"
            }
        }
    """.trimIndent()

    LogsScreen(branchData = sampleData)
}