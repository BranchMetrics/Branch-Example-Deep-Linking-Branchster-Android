package io.branch.branchster

import android.content.Intent
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import io.branch.branchster.navigation.NavGraph
import io.branch.branchster.navigation.Screen
import io.branch.branchster.ui.theme.BranchstersTheme
import io.branch.referral.BranchException
import io.branch.referral.shim.requestDeepLinkDataNullable
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private var branchData by mutableStateOf<String?>(null)
    private var isBranchInitialized by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            !isBranchInitialized
        }

        setContent {
            BranchstersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isBranchInitialized) {
                        val navController = rememberNavController()

                        LaunchedEffect(branchData) {
                            branchData?.let {
                                val encoded =
                                    URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                                navController.navigate(Screen.Details.createRoute(encoded))
                            }
                        }

                        NavGraph(navController = navController)
                    }
                }
            }
        }

        lifecycleScope.launch {
            try {
                val params = requestDeepLinkDataNullable(intent?.data)
                val clicked = params.optBoolean("+clicked_branch_link", false)
                if (clicked) {
                    Log.d("BranchSDK", "Deep link data: $params")
                    branchData = params.toString()
                } else {
                    Log.d("BranchSDK", "Opened app normally (no deep link)")
                }
            } catch (e: BranchException) {
                Log.e("BranchSDK", "Branch init error: ${e.branchError?.message}")
            }
            isBranchInitialized = true
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        val link = intent?.data ?: return
        lifecycleScope.launch {
            try {
                val params = requestDeepLinkDataNullable(link)
                val clicked = params.optBoolean("+clicked_branch_link", false)
                if (clicked) {
                    Log.d("BranchSDK", "Deep link data: $params")
                    branchData = params.toString()
                } else {
                    Log.d("BranchSDK", "Opened app normally (no deep link)")
                }
            } catch (e: BranchException) {
                Log.e("BranchSDK", "Branch init error: ${e.branchError.message}")
            }
        }
    }
}
