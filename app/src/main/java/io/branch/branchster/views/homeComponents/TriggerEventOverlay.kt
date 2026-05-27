package io.branch.branchster.views.homeComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.branch.branchster.ui.theme.imbPlexMonoFamily

@Composable
fun TriggerEventOverlay(
    monsterColor: String,
    monsterLevel: Int,
    monsterName: String,
    monsterExp: Int,
    onCreateEvent: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2D32)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎉 Trigger Branch Event",
                    style = TextStyle(
                        fontFamily = imbPlexMonoFamily,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Trigger an event to earn some XP for your monster!",
                style = TextStyle(
                    fontFamily = imbPlexMonoFamily,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            )

            Spacer(Modifier.height(16.dp))

            // Link display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Name: $monsterName",
                        overflow = TextOverflow.Clip,
                        style = TextStyle(
                            fontFamily = imbPlexMonoFamily,
                            color = Color(0xFF8A3FFC),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,

                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Color: $monsterColor",
                        overflow = TextOverflow.Clip,
                        style = TextStyle(
                            fontFamily = imbPlexMonoFamily,
                            color = Color(0xFF8A3FFC),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Level: $monsterLevel",
                        overflow = TextOverflow.Clip,
                        style = TextStyle(
                            fontFamily = imbPlexMonoFamily,
                            color = Color(0xFF8A3FFC),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "XP: $monsterExp",
                        overflow = TextOverflow.Clip,
                        style = TextStyle(
                            fontFamily = imbPlexMonoFamily,
                            color = Color(0xFF8A3FFC),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }

            Spacer(Modifier.height(16.dp))

            // Share button
            Button(
                onClick = onCreateEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111213)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Create Event",
                    style = TextStyle(
                        fontFamily = imbPlexMonoFamily,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}