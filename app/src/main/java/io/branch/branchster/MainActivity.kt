package io.branch.branchster

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.branch.branchster.navigation.NavGraph
import io.branch.branchster.navigation.Screen
import io.branch.branchster.ui.theme.BranchstersTheme
import io.branch.referral.Branch
import io.branch.referral.validators.IntegrationValidator
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private var branchData by mutableStateOf<String?>(null)
    private var isBranchInitialized by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // ✅ Don't launch Compose until Branch is initialized
        setContent {
            BranchstersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isBranchInitialized) {
                        val navController = rememberNavController()

                        // Navigate to deep link only after Branch init
                        LaunchedEffect(branchData) {
                            branchData?.let {
                                val encoded =
                                    URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                                navController.navigate(Screen.Details.createRoute(encoded))
                            }
                        }

                        NavGraph(navController = navController)
                    } else {
//                        // Optional loading UI while Branch initializes
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator()
//                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // ✅ Initialize Branch before UI navigation decisions
        Branch.sessionBuilder(this)
            .withCallback { referringParams, error ->
                if (error == null && referringParams != null) {
                    val clicked = referringParams.optBoolean("+clicked_branch_link", false)
                    if (clicked) {
                        Log.d("BranchSDK", "Deep link data: $referringParams")
                        branchData = referringParams.toString()
                    } else {
                        Log.d("BranchSDK", "Opened app normally (no deep link)")
                    }
                } else {
                    Log.e("BranchSDK", "Branch init error: ${error?.message}")
                }

                // ✅ Mark initialization complete so UI can load
                isBranchInitialized = true
            }
            .withData(intent?.data)
            .init()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent == null || intent.data == null) return

        if (intent.hasExtra("branch_force_new_session") &&
            intent.getBooleanExtra("branch_force_new_session", false)
        ) {
            Branch.sessionBuilder(this)
                .withCallback { referringParams, error ->
                    if (error == null && referringParams != null) {
                        branchData = referringParams.toString()
                        Log.i("BranchSDK_Tester", "ReInit: $referringParams")
                    } else {
                        Log.e("BranchSDK_Tester", error?.message ?: "Unknown error")
                    }
                }
                .reInit()
        }
    }
}


//
//class MainActivity : ComponentActivity() {
//
//    private var branchData by mutableStateOf<String?>(null)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            BranchstersTheme {
//
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController = rememberNavController()
//                    // Navigate to "deeplink" screen when new data comes
//                    LaunchedEffect(branchData) {
//                        branchData?.let {
//                            val encoded = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
//                            navController.navigate(Screen.Details.createRoute(encoded))
//                        }
//                    }
//                    NavGraph(navController = navController)
//                }
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        //IntegrationValidator.validate(this)
//
//        Branch.sessionBuilder(this)
//            .withCallback { referringParams, error ->
//                if (error == null && referringParams != null) {
//                    Log.d("BranchSDK", "Deep link data: $referringParams")
//                    branchData = referringParams.toString()
//                } else {
//                    Log.e("BranchSDK", "Branch init error: ${error?.message}")
//                }
//            }
//            .withData(intent?.data)
//            .init()
//    }
//
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//
//        this.setIntent(intent)
//
//        if (intent==null){
//            return
//        }
//        if (intent.data==null){
//            return
//        }
//
//        // Restore branchData if passed via intent
//        branchData = intent.getStringExtra("branch_data")
//
//        if (intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra(
//                "branch_force_new_session",
//                false
//            )
//        ) {
//            Branch.sessionBuilder(this).withCallback { referringParams, error ->
//                if (error != null) {
//                    Log.e("BranchSDK_Tester", error.message)
//                } else if (referringParams != null) {
//                    branchData = referringParams.toString()
//                    Log.i("BranchSDK_Tester", referringParams.toString())
//                }
//            }.reInit()
//
//        }
//    }
//
//}
