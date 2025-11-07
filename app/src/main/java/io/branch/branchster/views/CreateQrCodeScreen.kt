package io.branch.branchster.views

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.QRCode.BranchQRCode
import io.branch.referral.util.LinkProperties


@Composable
fun CreateQrCodeScreen(
    monsterColor: String,
    monsterLevel: Int,
    monsterName: String
) {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(monsterColor, monsterLevel, monsterName) {
        createQRCode(
            context = context,
            monsterColor = monsterColor,
            monsterLevel = monsterLevel,
            selectedMonsterName = monsterName,
            baseImageURL = "https://rob-gioia-branch.github.io/",
            imageURLSuffix = ".png"
        ) { bitmap ->
            qrBitmap = bitmap
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${monsterName.replaceFirstChar { it.uppercase() }} (Lvl $monsterLevel)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(240.dp)
            )
        } ?: CircularProgressIndicator(color = Color(0xFF007AFF))
    }
}

fun createQRCode(
    context: Context,
    monsterColor: String,
    monsterLevel: Int,
    selectedMonsterName: String,
    baseImageURL: String,
    imageURLSuffix: String,
    onComplete: (Bitmap?) -> Unit
) {
    val qrCode = BranchQRCode()

    when (monsterColor.lowercase()) {
        "green" -> qrCode.setCodeColor("#00FF00")
        "red" -> qrCode.setCodeColor("#FF0000")
        "blue" -> qrCode.setCodeColor("#0000FF")
        "yellow" -> qrCode.setCodeColor("#FFFF00")
        "purple" -> qrCode.setCodeColor("#800080")
        "white" -> qrCode.setCodeColor("#FFFFFF")
        "black" -> qrCode.setCodeColor("#000000")
        "pink" -> qrCode.setCodeColor("#FFC0CB")
        "orange" -> qrCode.setCodeColor("#FFA500")
        else -> qrCode.setCodeColor("#000000")
    }

    val bgColor = when (monsterColor.lowercase()) {
        "white", "yellow", "pink" -> Color.Black
        else -> Color.White
    }

    val imageUrl =
        "$baseImageURL${monsterColor.lowercase()}_monster_level_${monsterLevel}$imageURLSuffix"

    qrCode
        .setBackgroundColor(bgColor.toArgb())
        .setMargin(1)
        .setWidth(1024)
        .setImageFormat(BranchQRCode.BranchImageFormat.PNG)
        .setCenterLogo(imageUrl)

    val buo = BranchUniversalObject()
        .setCanonicalIdentifier("${monsterColor.lowercase()}/$monsterLevel")
        .setTitle("Monster: $selectedMonsterName")
        .setContentDescription("Level: $monsterLevel | Color: $monsterColor")
        .setContentImageUrl(imageUrl)

    val lp = LinkProperties()
//        .setChannel("qr")
//        .setFeature("sharing")
//        .setCampaign("monster_launch")
//        .setStage("new_user")

    val activity = context as? Activity
    if (activity == null) {
        Log.e("BranchQR", "Context is not an Activity!")
        onComplete(null)
        return
    }

    qrCode.getQRCodeAsImage(activity, buo, lp, object :
        BranchQRCode.BranchQRCodeImageHandler<Any?> {
        override fun onSuccess(qrCodeImage: Bitmap) {
            Log.d("BranchQR", "QR code created successfully.")
            onComplete(qrCodeImage)
        }

        override fun onFailure(e: Exception) {
            Log.e("BranchQR", "Error creating QR code: ${e.localizedMessage}")
            onComplete(null)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateQrCodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CreateQrCodeScreen(monsterColor = "orange", monsterLevel = 2, monsterName = "orange Monster")

    }
}