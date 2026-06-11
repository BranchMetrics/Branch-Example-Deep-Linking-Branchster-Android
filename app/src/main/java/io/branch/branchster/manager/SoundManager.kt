package io.branch.branchster.manager

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Global sound manager for playing game sounds with proper sequencing.
 * Handles progress upgrade sounds and level-up sounds with automatic delay
 * when both events occur simultaneously.
 */
class SoundManager private constructor(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private val soundQueue = mutableListOf<SoundEvent>()
    private val mutex = Mutex()
    private var isPlaying = false
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SoundManager? = null
        
        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    /**
     * Represents a sound event to be played
     */
    private data class SoundEvent(
        val soundType: SoundType,
        val delayMs: Long = 0,
        val onStart: (() -> Unit)? = null
    )
    
    /**
     * Types of sounds available in the game
     */
    enum class SoundType(val resourceName: String) {
        PROGRESS_UPGRADE("progress_upgrade"),
        LEVEL_UP("level_up")
    }
    
    /**
     * Plays a progress upgrade sound
     */
    fun playProgressSound() {
        scope.launch {
            enqueueSoundEvent(SoundEvent(SoundType.PROGRESS_UPGRADE))
        }
    }
    
    /**
     * Plays a level-up sound
     */
    fun playLevelUpSound() {
        scope.launch {
            enqueueSoundEvent(SoundEvent(SoundType.LEVEL_UP))
        }
    }
    
    /**
     * Plays progress sound followed by level-up sound with a delay
     * This is called when both events happen simultaneously
     * @param delayBetweenSoundsMs Delay between progress and level-up sounds
     * @param onLevelUpStart Callback invoked when level-up sound starts (for syncing animations)
     */
    fun playProgressThenLevelUp(delayBetweenSoundsMs: Long = 50, onLevelUpStart: (() -> Unit)? = null) {
        scope.launch {
            enqueueSoundEvent(SoundEvent(SoundType.PROGRESS_UPGRADE))
            enqueueSoundEvent(SoundEvent(SoundType.LEVEL_UP, delayMs = delayBetweenSoundsMs, onStart = onLevelUpStart))
        }
    }
    
    /**
     * Adds a sound event to the queue and processes it
     */
    private suspend fun enqueueSoundEvent(event: SoundEvent) {
        mutex.withLock {
            soundQueue.add(event)
        }
        processSoundQueue()
    }
    
    /**
     * Processes the sound queue sequentially
     */
    private suspend fun processSoundQueue() {
        if (isPlaying) return
        
        while (soundQueue.isNotEmpty()) {
            val event = mutex.withLock {
                if (soundQueue.isNotEmpty()) soundQueue.removeAt(0) else null
            } ?: break
            
            // Apply delay if specified
            if (event.delayMs > 0) {
                delay(event.delayMs)
            }
            
            playSound(event.soundType, event.onStart)
        }
    }
    
    /**
     * Plays a sound based on the sound type
     */
    private suspend fun playSound(soundType: SoundType, onStart: (() -> Unit)? = null) {
        try {
            isPlaying = true
            
            // Invoke callback when sound starts
            onStart?.invoke()
            
            // Get resource ID dynamically
            val resourceId = getResourceId(soundType.resourceName)
            
            if (resourceId == 0) {
                Log.w("SoundManager", "Sound resource not found: ${soundType.resourceName}")
                isPlaying = false
                return
            }
            
            // Release any existing media player
            releaseMediaPlayer()
            
            // Create and configure media player
            mediaPlayer = MediaPlayer.create(context, resourceId)
            
            if (mediaPlayer == null) {
                Log.e("SoundManager", "Failed to create MediaPlayer for: ${soundType.resourceName}")
                isPlaying = false
                return
            }
            
            // Set completion listener to continue processing queue
            mediaPlayer?.setOnCompletionListener {
                releaseMediaPlayer()
                isPlaying = false
                scope.launch {
                    processSoundQueue()
                }
            }
            
            // Set error listener
            mediaPlayer?.setOnErrorListener { _, what, extra ->
                Log.e("SoundManager", "MediaPlayer error: what=$what, extra=$extra")
                releaseMediaPlayer()
                isPlaying = false
                scope.launch {
                    processSoundQueue()
                }
                true
            }
            
            // Start playback
            mediaPlayer?.start()
            Log.d("SoundManager", "Playing sound: ${soundType.resourceName}")
            
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing sound: ${soundType.resourceName}", e)
            releaseMediaPlayer()
            isPlaying = false
            scope.launch {
                processSoundQueue()
            }
        }
    }
    
    /**
     * Gets the resource ID for a sound file in the raw directory
     */
    private fun getResourceId(resourceName: String): Int {
        return try {
            context.resources.getIdentifier(resourceName, "raw", context.packageName)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error getting resource ID for: $resourceName", e)
            0
        }
    }
    
    /**
     * Releases the media player resources
     */
    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing media player", e)
        }
    }
    
    /**
     * Clears the sound queue and stops any playing sounds
     */
    fun clearQueue() {
        scope.launch {
            mutex.withLock {
                soundQueue.clear()
            }
            releaseMediaPlayer()
            isPlaying = false
        }
    }
    
    /**
     * Releases all resources. Call this when the app is destroyed.
     */
    fun release() {
        clearQueue()
        releaseMediaPlayer()
    }
}
