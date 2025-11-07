package io.branch.branchster.api

import io.branch.branchster.manager.GeminiImagenManager
import io.branch.branchster.models.ImageGenerationRequest
import io.branch.branchster.models.ImageGenerationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * API wrapper for Gemini Imagen operations
 * Provides a clean interface for image generation throughout the app
 */
object GeminiImagenApi {
    
    /**
     * Generate an image from a simple text prompt
     * @param prompt The text description of the image
     * @return ImageGenerationResponse with success status and image data
     */
    suspend fun generateImage(prompt: String): ImageGenerationResponse {
        return withContext(Dispatchers.IO) {
            try {
                if (!GeminiImagenManager.isReady()) {
                    return@withContext ImageGenerationResponse(
                        success = false,
                        imageData = null,
                        errorMessage = "Gemini Imagen is not initialized"
                    )
                }
                
                val imageData = GeminiImagenManager.generateImage(prompt)
                
                if (imageData != null) {
                    ImageGenerationResponse(
                        success = true,
                        imageData = imageData,
                        errorMessage = null
                    )
                } else {
                    ImageGenerationResponse(
                        success = false,
                        imageData = null,
                        errorMessage = "Failed to generate image"
                    )
                }
            } catch (e: Exception) {
                ImageGenerationResponse(
                    success = false,
                    imageData = null,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Generate an image with custom parameters
     * @param request ImageGenerationRequest with all parameters
     * @return ImageGenerationResponse with success status and image data
     */
    suspend fun generateImageWithParams(request: ImageGenerationRequest): ImageGenerationResponse {
        return withContext(Dispatchers.IO) {
            try {
                if (!GeminiImagenManager.isReady()) {
                    return@withContext ImageGenerationResponse(
                        success = false,
                        imageData = null,
                        errorMessage = "Gemini Imagen is not initialized"
                    )
                }
                
                val imageData = GeminiImagenManager.generateImageWithParams(
                    prompt = request.prompt,
                    aspectRatio = request.aspectRatio,
                    negativePrompt = request.negativePrompt
                )
                
                if (imageData != null) {
                    ImageGenerationResponse(
                        success = true,
                        imageData = imageData,
                        errorMessage = null
                    )
                } else {
                    ImageGenerationResponse(
                        success = false,
                        imageData = null,
                        errorMessage = "Failed to generate image"
                    )
                }
            } catch (e: Exception) {
                ImageGenerationResponse(
                    success = false,
                    imageData = null,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Check if the API is ready to use
     */
    fun isReady(): Boolean = GeminiImagenManager.isReady()
}
