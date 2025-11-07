package io.branch.branchster.models

import android.graphics.Bitmap

/**
 * Data class for image generation responses
 * @param success Whether the image generation was successful
 * @param imageData The generated image as a Bitmap
 * @param errorMessage Error message if generation failed
 */
data class ImageGenerationResponse(
    val success: Boolean,
    val imageData: Bitmap?,
    val errorMessage: String?
)
