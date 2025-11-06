package io.branch.branchsters

import android.app.Application
import io.branch.branchsters.data.AppDatabase
import io.branch.branchsters.data.repository.MonsterRepository
import io.branch.branchsters.data.repository.QuestRepository
import io.branch.branchsters.manager.GeminiImagenManager
import io.branch.referral.Branch
import io.branch.referral.BranchLogger

class ApplicationClass: Application() {
    
    // Database instance
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Repositories
    val questRepository by lazy { QuestRepository(database.questDao()) }
    val monsterRepository by lazy { MonsterRepository(database.monsterDao()) }
    
    override fun onCreate() {
        super.onCreate()
        // Branch logging for debugging
        Branch.enableLogging(BranchLogger.BranchLogLevel.VERBOSE)

        // Branch object initialization
        Branch.getAutoInstance(this)
        
        // Initialize Gemini Imagen Manager for global access
        GeminiImagenManager.initialize(this)
    }
}