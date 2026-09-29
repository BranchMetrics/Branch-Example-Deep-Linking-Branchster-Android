package io.branch.branchster

import android.app.Application
import android.util.Log
import io.branch.branchster.data.AppDatabase
import io.branch.branchster.data.repository.BranchEventRepository
import io.branch.branchster.data.repository.MonsterRepository
import io.branch.branchster.data.repository.QuestRepository
import io.branch.branchster.manager.SoundManager
import io.branch.interfaces.IBranchLoggingCallbacks
import io.branch.referral.Branch
import io.branch.referral.BranchConfiguration
import io.branch.referral.BranchLogger.BranchLogLevel
import io.branch.referral.DMAParameters
import io.branch.referral.Defines


class ApplicationClass: Application() {
    
    // Database instance
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Repositories
    val questRepository by lazy { QuestRepository(database.questDao()) }
    val monsterRepository by lazy { MonsterRepository(database.monsterDao()) }
    val branchEventRepository by lazy { BranchEventRepository(database.branchEventDao()) }
    
    // Sound Manager for global access
    val soundManager by lazy { SoundManager.getInstance(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Branch logging for debugging
        //Branch.enableLogging(BranchLogger.BranchLogLevel.VERBOSE)

//        val dma = DMAParameters.Builder()
//            .setEeaRegion(false)
//            .setAdUserDataUsageConsent(true)
//            .build()

        val config = BranchConfiguration.Builder("key_live_mbErCMtrzeheAWS0Xagg7hjbwDkaZ6SP")
            .setLogLevel(BranchLogLevel.VERBOSE)
            .setLoggingCallback(IBranchLoggingCallbacks { message: String?, tag: String? ->
                Log.d(
                    "Branch",
                    message!!
                )
            })
            .setUserAgentFetchSync(true)
            .setNetworkTimeout(5000)
//            .setDMAParameters(dma)
            .build()

        Branch.initialize(this, config)

//        Branch.getInstance().setDMAParameters(
//            dma.toBuilder()
//                .setAdPersonalizationConsent(true)
//                .build());

//        val config2 = BranchConfiguration.Builder("key_live_mbErCMtrzeheAWS0Xagg7hjbwDkaZ6SP").build()
//        Branch.initialize(this, config2)
    }
}