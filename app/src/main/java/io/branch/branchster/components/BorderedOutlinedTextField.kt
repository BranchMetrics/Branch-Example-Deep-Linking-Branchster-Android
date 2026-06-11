package io.branch.branchster.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.branch.branchster.ui.theme.imbPlexMonoFamily

@Composable
fun BorderedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    fontSize: TextUnit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = fontSize,
                fontFamily = imbPlexMonoFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        textStyle = TextStyle(
            color = Color.White,
            fontSize = fontSize,
            fontFamily = imbPlexMonoFamily,
            fontWeight = FontWeight.Medium
        ),
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        singleLine = singleLine
    )
}
