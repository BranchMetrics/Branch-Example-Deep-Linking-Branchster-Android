package io.branch.branchster.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.branch.branchster.R

val imbPlexMonoFamily = FontFamily(
    Font(R.font.ibmplexmono_light, FontWeight.Light),
    Font(R.font.ibmplexmono_regular, FontWeight.Normal),
    Font(R.font.ibmplexmono_semi_medium, FontWeight.Medium),
    Font(R.font.ibmplexmono_semi_bold, FontWeight.SemiBold),
    Font(R.font.ibmplexmono_bold, FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
