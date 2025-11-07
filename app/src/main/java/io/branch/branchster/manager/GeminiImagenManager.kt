package io.branch.branchster.manager

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Singleton manager for Firebase AI Gemini Imagen API
 * Uses Firebase AI Logic SDK with Google AI backend (Free Tier)
 * Provides global access to image generation capabilities
 */
object GeminiImagenManager {
    
    private var imagenModel: ImagenModel? = null
    private var isInitialized = false
    
    /**
     * Initialize the Gemini Imagen SDK with Google AI backend (Free Tier)
     * Call this in Application.onCreate()
     * Note: Requires GEMINI_API_KEY in BuildConfig or environment
     */
    fun initialize(context: Context) {
        if (isInitialized) {
            return
        }
        
        try {
            // Initialize Firebase AI with Google AI backend (Free Tier)
            val ai = Firebase.ai(backend = GenerativeBackend.googleAI())
            
            // Create ImagenModel instance with Imagen 3.0 Fast (Free tier available)
            imagenModel = ai.imagenModel("imagen-3.0-fast-generate-001")
            isInitialized = true
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize Gemini Imagen: ${e.message}", e)
        }
    }
    
    /**
     * Generate an image from a text prompt
     * @param prompt The text description of the image to generate
     * @return Bitmap or null if generation fails
     */
    suspend fun generateImage(prompt: String): Bitmap? = withContext(Dispatchers.IO) {
        if (!isInitialized || imagenModel == null) {
            throw IllegalStateException("GeminiImagenManager not initialized. Call initialize() first.")
        }
        
        try {
            // Generate image using Imagen model
            val response = imagenModel!!.generateImages(prompt)
            
            // Extract the generated image as Bitmap
            response.images.firstOrNull()?.asBitmap()
        } catch (e: Exception) {
            // Log error and return null
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Generate an image with custom parameters
     * @param prompt The text description
     * @param aspectRatio Optional aspect ratio (e.g., "16:9", "1:1", "9:16")
     * @param negativePrompt Optional negative prompt to avoid certain elements
     * @return Bitmap or null if generation fails
     */
    suspend fun generateImageWithParams(
        prompt: String,
        aspectRatio: String? = null,
        negativePrompt: String? = null
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (!isInitialized || imagenModel == null) {
            throw IllegalStateException("GeminiImagenManager not initialized. Call initialize() first.")
        }
        
        try {
            // Build enhanced prompt with parameters
            val enhancedPrompt = buildString {
                append(prompt)
                aspectRatio?.let { append(" [Aspect Ratio: $it]") }
                negativePrompt?.let { append(" [Avoid: $it]") }
            }
            
            // Generate image with enhanced prompt
            val response = imagenModel!!.generateImages(enhancedPrompt)
            
            // Extract the generated image data
            response.images.firstOrNull()?.asBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Check if the manager is initialized and ready to use
     */
    fun isReady(): Boolean = isInitialized && imagenModel != null
    
    /**
     * Get the current model instance (for advanced usage)
     */
    fun getModel(): ImagenModel? = imagenModel
}
