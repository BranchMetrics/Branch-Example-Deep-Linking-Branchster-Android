package io.branch.branchsters.views.homeComponents

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.branch.branchsters.data.entity.BranchEventData
import io.branch.branchsters.ui.theme.ibmPlexMono
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ViewBranchDataOverlay(
    onGetLatestEvent: suspend () -> BranchEventData?,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    var eventData by remember { mutableStateOf<BranchEventData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        eventData = withContext(Dispatchers.IO) {
            onGetLatestEvent()
        }
        isLoading = false
    }

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
                    text = "📊 View Branch Event Data",
                    style = TextStyle(
                        fontFamily = ibmPlexMono,
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
                text = "View the latest Branch event data stored in the database!",
                style = TextStyle(
                    fontFamily = ibmPlexMono,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            )

            Spacer(Modifier.height(16.dp))

            // Event data display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(12.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2FB8FF),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else if (eventData != null) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Event Name: ${eventData!!.eventName}",
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Monster Name: ${eventData!!.monsterName}",
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Monster Color: ${eventData!!.monsterColor}",
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Monster Level: ${eventData!!.monsterLevel}",
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Monster XP: ${eventData!!.monsterExp}",
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
                        val formattedDate = dateFormat.format(Date(eventData!!.timestamp))
                        
                        Text(
                            text = "Timestamp: $formattedDate",
                            overflow = TextOverflow.Clip,
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color(0xFF2FB8FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 2
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No event data found.\nPlease trigger an event first.",
                            style = TextStyle(
                                fontFamily = ibmPlexMono,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Complete button
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111213)
                ),
                enabled = eventData != null && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Complete",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Complete Quest",
                    style = TextStyle(
                        fontFamily = ibmPlexMono,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
