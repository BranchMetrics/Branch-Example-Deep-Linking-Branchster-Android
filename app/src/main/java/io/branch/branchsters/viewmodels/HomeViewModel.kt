package io.branch.branchsters.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.branch.branchsters.data.entity.BranchEventData
import io.branch.branchsters.data.repository.BranchEventRepository
import io.branch.branchsters.data.repository.MonsterRepository
import io.branch.branchsters.data.repository.QuestRepository
import io.branch.branchsters.manager.SoundManager
import io.branch.branchsters.models.HomeUiState
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.QRCode.BranchQRCode
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.LinkProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class HomeViewModel(
    private val questRepository: QuestRepository,
    private val monsterRepository: MonsterRepository,
    private val branchEventRepository: BranchEventRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var currentQuestID = 0
    private var pendingShareQuestId: Int? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                monsterRepository.currentMonster,
                questRepository.allQuests
            ) { monster, quests ->
                _uiState.value.copy(
                    currentMonster = monster,
                    quests = quests,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun toggleQuestCompletion(questId: Int) {
        viewModelScope.launch {
            val quest = questRepository.getQuestById(questId)
            val currentMonster = _uiState.value.currentMonster

            quest?.let {
                if (!it.isCompleted && !it.isLocked) {
                    // Mark quest as completed
                    questRepository.updateQuestCompletion(questId, true)

                    // Unlock any quests that depend on this quest
                    questRepository.unlockDependentQuests(questId)

                    // Add XP and update level
                    currentMonster?.let { monster ->
                        val newXp = monster.xp + 50 // Each quest = 50 XP
                        val completedQuests = _uiState.value.quests.count { q -> q.isCompleted } + 1
                        val newLevel = calculateLevel(completedQuests)
                        val newImage = getMonsterImageForLevel(monster.monsterName, newLevel)
                        
                        // Check if level changed
                        val leveledUp = newLevel > monster.level

                        if (leveledUp) {
                            // First update XP only (no visual change yet)
                            val xpOnlyUpdate = monster.copy(xp = newXp)
                            monsterRepository.updateMonster(xpOnlyUpdate)
                            
                            // Play progress sound, then level-up sound with callback to update visual
                            soundManager.playProgressThenLevelUp(
                                onLevelUpStart = {
                                    // Update level and image when level-up sound starts
                                    viewModelScope.launch {
                                        val finalUpdate = monster.copy(
                                            xp = newXp,
                                            level = newLevel,
                                            monsterImage = newImage
                                        )
                                        monsterRepository.updateMonster(finalUpdate)
                                    }
                                }
                            )
                        } else {
                            // Just progress - update everything immediately
                            val updatedMonster = monster.copy(
                                xp = newXp,
                                level = newLevel,
                                monsterImage = newImage
                            )
                            monsterRepository.updateMonster(updatedMonster)
                            soundManager.playProgressSound()
                        }
                        
                        currentQuestID = 0;
                    }
                }
            }
        }
    }


    fun refreshProgress(questId: Int) {
        viewModelScope.launch {
            val quest = questRepository.getQuestById(questId)
            val currentMonster = _uiState.value.currentMonster

            quest?.let {
                if (it.isCompleted) {
                    // Unlock any quests that depend on this quest
                    questRepository.unlockDependentQuests(questId)

                    // Add XP and update level
                    currentMonster?.let { monster ->
                        val newXp = monster.xp + 50 // Each quest = 50 XP
                        val completedQuests = _uiState.value.quests.count { q -> q.isCompleted }
                        val newLevel = calculateLevel(completedQuests)
                        val newImage = getMonsterImageForLevel(monster.monsterName, newLevel)
                        
                        // Check if level changed
                        val leveledUp = newLevel > monster.level

                        if (leveledUp) {
                            // First update XP only (no visual change yet)
                            val xpOnlyUpdate = monster.copy(xp = newXp)
                            monsterRepository.updateMonster(xpOnlyUpdate)
                            
                            // Play progress sound, then level-up sound with callback to update visual
                            soundManager.playProgressThenLevelUp(
                                onLevelUpStart = {
                                    // Update level and image when level-up sound starts
                                    viewModelScope.launch {
                                        val finalUpdate = monster.copy(
                                            xp = newXp,
                                            level = newLevel,
                                            monsterImage = newImage
                                        )
                                        monsterRepository.updateMonster(finalUpdate)
                                    }
                                }
                            )
                        } else {
                            // Just progress - update everything immediately
                            val updatedMonster = monster.copy(
                                xp = newXp,
                                level = newLevel,
                                monsterImage = newImage
                            )
                            monsterRepository.updateMonster(updatedMonster)
                            soundManager.playProgressSound()
                        }
                        
                        currentQuestID = 0;
                    }
                }
            }
        }
    }

    // Link Sharing methods
    fun openOverlayForLinkShare(questId: Int){
        currentQuestID = questId
        val branchLink = _uiState.value.currentMonster?.branchLink
        val shouldShowOverlay = !branchLink.isNullOrEmpty() && (_uiState.value.quests.find { it.id == questId }?.isCompleted == false)
        _uiState.update { it.copy(showOverlay = shouldShowOverlay, branchLink = branchLink ?: "") }
    }

    fun dismissShareOverlay(){
        currentQuestID = 0
        _uiState.update { it.copy(showOverlay = false) }
    }

    fun onLinkShared() {
        // Set pending share quest ID before launching share sheet
        pendingShareQuestId = currentQuestID
        _uiState.update { it.copy(showOverlay = false) }
        Log.d("ShareBranchLink", "Share sheet opened for quest $currentQuestID")
    }


    // Trigger event methods
    fun openOverlayForTriggerEvent(questId: Int){
        currentQuestID = questId
        val shouldShowOverlay = _uiState.value.quests.find { it.id == questId }?.isCompleted == false
        _uiState.update { it.copy(showTriggerEventOverlay = shouldShowOverlay) }
    }

    fun dismissEventOverlay(){
        currentQuestID = 0
        _uiState.update { it.copy(showTriggerEventOverlay = false) }
    }

    // View Branch Data methods
    fun openOverlayForViewBranchData(questId: Int){
        currentQuestID = questId
        val shouldShowOverlay = _uiState.value.quests.find { it.id == questId }?.isCompleted == false
        _uiState.update { it.copy(showViewBranchDataOverlay = shouldShowOverlay) }
    }

    fun dismissViewBranchDataOverlay(){
        currentQuestID = 0
        _uiState.update { it.copy(showViewBranchDataOverlay = false) }
    }

    fun completeViewBranchData(){
        viewModelScope.launch {
            _uiState.update { it.copy(showViewBranchDataOverlay = false) }
            val quest = questRepository.getQuestById(currentQuestID)
            quest?.let {
                if (!it.isCompleted && !it.isLocked) {
                    // Mark quest as completed
                    questRepository.updateQuestCompletion(currentQuestID, true)
                    refreshProgress(currentQuestID)
                }
            }
        }
    }

    suspend fun getLatestBranchEvent(): BranchEventData? {
        return branchEventRepository.getLatestBranchEvent()
    }

    fun createBranchEvent(context: Context) {
        viewModelScope.launch {
            val monsterName = _uiState.value.currentMonster?.monsterTitle ?: ""
            val monsterColor = _uiState.value.currentMonster?.monsterTitle?.split(" ")?.first() ?: ""
            val monsterLevel = _uiState.value.currentMonster?.level ?: 0
            val monsterExp = _uiState.value.currentMonster?.xp ?: 0
            
            val result  = trackEvent(
                monsterName = monsterName,
                monsterColor = monsterColor,
                monsterLevel = monsterLevel,
                monsterExp = monsterExp,
                context = context,
            )

            result.let {
                // Store the event data in the database
                val eventData = BranchEventData(
                    eventName = monsterName,
                    monsterName = monsterName,
                    monsterColor = monsterColor,
                    monsterLevel = monsterLevel,
                    monsterExp = monsterExp
                )
                branchEventRepository.insertBranchEvent(eventData)
                
                _uiState.update { it.copy(showTriggerEventOverlay = false) }
                val quest = questRepository.getQuestById(currentQuestID)
                quest?.let {
                    if (!it.isCompleted && !it.isLocked) {
                        // Mark quest as completed
                        questRepository.updateQuestCompletion(currentQuestID, true)
                        refreshProgress(currentQuestID)
                    }
                }
            }
        }
    }

    fun trackEvent(
        monsterColor: String,
        monsterLevel: Int,
        monsterName: String,
        monsterExp: Int,
        context: Context
    ): BranchEvent {
        val event = BranchEvent(monsterName)
            .addCustomDataProperty("Monster Name", monsterName)
            .addCustomDataProperty("Monster Color", monsterColor)
            .addCustomDataProperty("Monster Level", monsterLevel.toString())
            .addCustomDataProperty("Monster Exp", monsterExp.toString())

        event.logEvent(context)
        return event
    }

    fun generateQrCode(
        questId: Int,
        context: Context,
        monsterColor: String,
        monsterLevel: Int,
        monsterName: String,
    ) {
        createQRCode(
            context = context,
            monsterColor = monsterColor,
            monsterLevel = monsterLevel,
            monsterName = monsterName,
            baseImageURL = "https://rob-gioia-branch.github.io/",
            imageURLSuffix = ".png"
        ) { bitmap ->
            currentQuestID = questId
            bitmap?.let {
                // Persist the QR code to storage
                saveQrCodeToStorage(context, it, monsterName)
            }
            _uiState.update { it.copy(qrCodeImage = bitmap, showQrCodeOverlay = true) }
        }
    }

    fun createQRCode(
        context: Context,
        monsterColor: String,
        monsterLevel: Int,
        monsterName: String,
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
            .setTitle("Monster: $monsterName")
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


    fun dismissQrCodeOverlay(){
        currentQuestID = 0
        _uiState.update { it.copy(showQrCodeOverlay = false) }
    }

    fun completeQrCode(){
        viewModelScope.launch {
            _uiState.update { it.copy(showQrCodeOverlay = false) }
            val quest = questRepository.getQuestById(currentQuestID)
            quest?.let {
                if (!it.isCompleted && !it.isLocked) {
                    // Mark quest as completed
                    questRepository.updateQuestCompletion(currentQuestID, true)
                    refreshProgress(currentQuestID)
                }
            }
        }
    }
    
    /**
     * Saves QR code bitmap to internal storage and updates the monster's qrCodePath
     */
    private fun saveQrCodeToStorage(context: Context, bitmap: Bitmap, monsterName: String) {
        viewModelScope.launch {
            try {
                val fileName = "qr_code_${monsterName.replace(" ", "_")}.png"
                val file = File(context.filesDir, fileName)
                
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                
                // Update the monster's QR code path in database
                _uiState.value.currentMonster?.let { monster ->
                    monsterRepository.updateQrCodePath(monster.id, file.absolutePath)
                    Log.d("QRCodePersistence", "QR code saved to: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                Log.e("QRCodePersistence", "Error saving QR code: ${e.localizedMessage}")
            }
        }
    }
    
    /**
     * Loads a persisted QR code from storage
     * Returns the bitmap if found, null otherwise
     */
    fun loadPersistedQrCode(context: Context): Bitmap? {
        return try {
            val qrCodePath = _uiState.value.currentMonster?.qrCodePath
            if (qrCodePath != null) {
                val file = File(qrCodePath)
                if (file.exists()) {
                    android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                } else {
                    Log.w("QRCodePersistence", "QR code file not found at: $qrCodePath")
                    null
                }
            } else {
                Log.w("QRCodePersistence", "No QR code path stored for current monster")
                null
            }
        } catch (e: Exception) {
            Log.e("QRCodePersistence", "Error loading QR code: ${e.localizedMessage}")
            null
        }
    }


    fun shareQrCode(questId: Int, context: Context){
        currentQuestID = questId
        shareBranchQrCode(context)
    }

    fun shareBranchQrCode(context: Context) {
        viewModelScope.launch {
            try {
                // Get the persisted QR code path from the current monster
                val qrCodePath = _uiState.value.currentMonster?.qrCodePath
                
                if (qrCodePath.isNullOrEmpty()) {
                    Toast.makeText(context, "No QR code found. Please generate one first.", Toast.LENGTH_SHORT).show()
                    Log.w("ShareBranchQrCode", "No QR code path stored for current monster")
                    return@launch
                }
                
                val qrCodeFile = File(qrCodePath)
                
                if (!qrCodeFile.exists()) {
                    Toast.makeText(context, "QR code file not found. Please regenerate.", Toast.LENGTH_SHORT).show()
                    Log.w("ShareBranchQrCode", "QR code file not found at: $qrCodePath")
                    return@launch
                }
                
                val shareText = "Scan my QR code to view my Level ${_uiState.value.currentMonster?.level} monster, '${_uiState.value.currentMonster?.monsterTitle}'!"
                
                // Get URI for the persisted file using FileProvider
                val imageUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    qrCodeFile
                )
                
                // Create share intent
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                // Set pending share quest ID before launching share sheet
                pendingShareQuestId = currentQuestID
                
                // Launch share sheet
                context.startActivity(
                    Intent.createChooser(shareIntent, "Share QR Code")
                )
                
                Log.d("ShareBranchQrCode", "Share sheet opened for quest $currentQuestID")
                
            } catch (e: Exception) {
                Toast.makeText(context, "QR Code share failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ShareBranchQrCode", "Error sharing QR code", e)
            }
        }
    }
    
    /**
     * Call this method when the app resumes (e.g., after share sheet is closed)
     * to complete the pending share quest
     */
    fun onAppResumed() {
        pendingShareQuestId?.let { questId ->
            viewModelScope.launch {
                val quest = questRepository.getQuestById(questId)
                val currentMonster = _uiState.value.currentMonster
                
                quest?.let {
                    if (!it.isCompleted && !it.isLocked) {
                        // Mark quest as completed
                        questRepository.updateQuestCompletion(questId, true)
                        
                        // Unlock any quests that depend on this quest
                        questRepository.unlockDependentQuests(questId)
                        
                        // Add XP and update level
                        currentMonster?.let { monster ->
                            val newXp = monster.xp + 50 // Each quest = 50 XP
                            val completedQuests = _uiState.value.quests.count { q -> q.isCompleted } + 1
                            val newLevel = calculateLevel(completedQuests)
                            val newImage = getMonsterImageForLevel(monster.monsterName, newLevel)
                            
                            // Check if level changed
                            val leveledUp = newLevel > monster.level
                            
                            if (leveledUp) {
                                // First update XP only (no visual change yet)
                                val xpOnlyUpdate = monster.copy(xp = newXp)
                                monsterRepository.updateMonster(xpOnlyUpdate)
                                
                                // Play progress sound, then level-up sound with callback to update visual
                                soundManager.playProgressThenLevelUp(
                                    onLevelUpStart = {
                                        // Update level and image when level-up sound starts
                                        viewModelScope.launch {
                                            val finalUpdate = monster.copy(
                                                xp = newXp,
                                                level = newLevel,
                                                monsterImage = newImage
                                            )
                                            monsterRepository.updateMonster(finalUpdate)
                                        }
                                    }
                                )
                            } else {
                                // Just progress - update everything immediately
                                val updatedMonster = monster.copy(
                                    xp = newXp,
                                    level = newLevel,
                                    monsterImage = newImage
                                )
                                monsterRepository.updateMonster(updatedMonster)
                                soundManager.playProgressSound()
                            }
                        }
                        
                        Log.d("ShareQuest", "Quest $questId marked as completed after share")
                    }
                }
                // Clear the pending quest
                pendingShareQuestId = null
                currentQuestID = 0
            }
        }
    }



    private fun calculateLevel(completedQuests: Int): Int {
        return when {
            completedQuests >= 6 -> 4
            completedQuests >= 4 -> 3
            completedQuests >= 2 -> 2
            else -> 1
        }
    }

    private fun getMonsterImageForLevel(monsterName: String, level: Int): Int {
        // Extract color from monsterName (e.g., "black_monster_level_1" -> "black")
        // Format: {color}_monster_level_{level}
        val colorPrefix = monsterName.substringBefore("_monster_level_")
        val resourceName = "${colorPrefix}_monster_level_${level}"

        return try {
            val context = io.branch.branchsters.R.drawable::class.java
            val field = context.getField(resourceName)
            field.getInt(null)
        } catch (e: Exception) {
            // Fallback to current image if resource not found
            _uiState.value.currentMonster?.monsterImage ?: io.branch.branchsters.R.drawable.onboard_monster_1
        }
    }

}


class HomeViewModelFactory(
    private val questRepository: QuestRepository,
    private val monsterRepository: MonsterRepository,
    private val branchEventRepository: BranchEventRepository,
    private val soundManager: SoundManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(questRepository, monsterRepository, branchEventRepository, soundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
