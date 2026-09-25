package io.branch.branchster

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import io.branch.branchster.navigation.NavGraph
import io.branch.branchster.ui.theme.BranchstersTheme
import io.branch.referral.BranchException
import io.branch.referral.shim.requestDeepLinkDataNullable
import io.branch.referral.validators.IntegrationValidator
import kotlinx.coroutines.launch

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

                        // Deep link data is captured here as soon as Branch resolves it, but it is
                        // NOT navigated to yet. NavGraph only acts on it once the user reaches Home,
                        // which happens after onboarding completes (or immediately for users who are
                        // already onboarded). This prevents a deferred deep link from interrupting
                        // first-run onboarding.
                        NavGraph(
                            navController = navController,
                            pendingDeepLink = branchData,
                            onDeepLinkConsumed = { branchData = null }
                        )
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
        IntegrationValidator.validate(this)
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
