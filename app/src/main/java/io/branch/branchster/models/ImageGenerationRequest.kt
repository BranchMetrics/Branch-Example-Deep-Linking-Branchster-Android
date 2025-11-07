package io.branch.branchster.models

/**
 * Data class for image generation requests
 * @param prompt The main text description of the image to generate
 * @param aspectRatio Optional aspect ratio (e.g., "1:1", "16:9", "9:16")
 * @param negativePrompt Optional text describing what to avoid in the image
 */
data class ImageGenerationRequest(
    val prompt: String,
    val aspectRatio: String? = null,
    val negativePrompt: String? = null
)
