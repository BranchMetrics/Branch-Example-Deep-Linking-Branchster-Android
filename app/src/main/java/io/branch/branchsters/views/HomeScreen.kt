import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.branch.branchsters.R
import io.branch.branchsters.ui.theme.imbPlexMonoFamily

@Composable
fun HomepageScreen(navController: NavController) { // Added NavController for navigation

    // Replaces the base Container with gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2A2D32), Color(0xFF1D1D1D)),
                    start = Offset.Zero, // Top-Left
                    end = Offset.Infinite // Bottom-Right
                )
            )
    ) {
        // Replaces SafeArea + ListView
        // .verticalScroll makes the Column scrollable, replacing ListView
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding() // Replaces SafeArea
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border( // Replaces border: Border.all()
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp)) // Ensures background respects the border radius
                    .background(Color.White.copy(alpha = 0.1f)) // Replaces color: Colors.white10
                    .padding(20.dp), // Replaces padding: EdgeInsets.all(20)
                horizontalAlignment = Alignment.CenterHorizontally // To center the image
            ) {
                Image(
                    painter = painterResource(R.drawable.monster_truck),
                    contentDescription = "Monster Truck",
                    modifier = Modifier.height(200.dp),
                    contentScale = ContentScale.Fit
                )

                // This Column replaces Align + Column
                Column(
                    modifier = Modifier.fillMaxWidth() // Ensures children fill the width
                ) {
                    // Replaces Row for Level and XP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level 1",
                            style = TextStyle(
                                fontFamily = imbPlexMonoFamily,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        )
                        Spacer(Modifier.weight(1f)) // Replaces Spacer()
                        Text(
                            text = "XP 250/500",
                            style = TextStyle(
                                fontFamily = imbPlexMonoFamily,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress =  0.5f ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp) // It's good practice to set a height
                            .clip(RoundedCornerShape(4.dp)), // Makes the progress bar rounded
                        color = Color(0xffEA2D7F),
                        trackColor = Color.White.copy(alpha = 0.3f) // Sets the background of the bar
                    )
                }
            }


            Spacer(Modifier.height(16.dp))
        }
    }
}