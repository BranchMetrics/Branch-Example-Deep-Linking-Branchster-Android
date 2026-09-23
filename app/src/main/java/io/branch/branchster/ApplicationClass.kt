package io.branch.branchster

import android.app.Application
import io.branch.branchster.data.AppDatabase
import io.branch.branchster.data.repository.BranchEventRepository
import io.branch.branchster.data.repository.MonsterRepository
import io.branch.branchster.data.repository.QuestRepository
import io.branch.branchster.manager.SoundManager
import io.branch.referral.BranchConfiguration

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

        // Branch object initialization
        val key: String = if (BuildConfig.DEBUG)
            BuildConfig.BRANCH_KEY_TEST
        else
            BuildConfig.BRANCH_KEY

        val config : BranchConfiguration = BranchConfiguration.Builder(key).build()
    }
}