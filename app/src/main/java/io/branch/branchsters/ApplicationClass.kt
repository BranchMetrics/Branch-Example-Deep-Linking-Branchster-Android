package io.branch.branchsters

import android.app.Application
import io.branch.referral.Branch
import io.branch.referral.BranchLogger

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        // Branch logging for debugging
        Branch.enableLogging(BranchLogger.BranchLogLevel.VERBOSE)

        // Branch object initialization
        Branch.getAutoInstance(this)
    }
}