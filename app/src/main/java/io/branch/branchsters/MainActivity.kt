package io.branch.branchsters

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
import androidx.navigation.compose.rememberNavController
import io.branch.branchsters.navigation.NavGraph
import io.branch.branchsters.ui.theme.BranchstersTheme
import io.branch.referral.Branch

class MainActivity : ComponentActivity() {

    private var branchData by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BranchstersTheme {

                // Navigate to "deeplink" screen when new data comes
                /*LaunchedEffect(branchData) {
                    branchData?.let {
                        navController.navigate("deeplink")
                    }
                }*/

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Branch.sessionBuilder(this)
            .withCallback { referringParams, error ->
                if (error == null && referringParams != null) {
                    Log.d("BranchSDK", "Deep link data: $referringParams")
                    branchData = referringParams.toString()
                } else {
                    Log.e("BranchSDK", "Branch init error: ${error?.message}")
                }
            }
            .withData(intent?.data)
            .init()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        this.setIntent(intent)

        if (intent==null){
            return
        }
        if (intent.data==null){
            return
        }

        // Restore branchData if passed via intent
        branchData = intent.getStringExtra("branch_data")

        if (intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra(
                "branch_force_new_session",
                false
            )
        ) {
            Branch.sessionBuilder(this).withCallback { referringParams, error ->
                if (error != null) {
                    Log.e("BranchSDK_Tester", error.message)
                } else if (referringParams != null) {
                    branchData = referringParams.toString()
                    Log.i("BranchSDK_Tester", referringParams.toString())
                }
            }.reInit()

        }
    }

}
