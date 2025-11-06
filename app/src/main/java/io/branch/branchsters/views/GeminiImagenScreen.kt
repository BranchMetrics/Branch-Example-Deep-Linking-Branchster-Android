package io.branch.branchsters.views

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import io.branch.branchsters.api.GeminiImagenApi
import io.branch.branchsters.models.ImageGenerationRequest
import kotlinx.coroutines.launch

/**
 * Example usage of Gemini Imagen API
 * This file demonstrates different ways to use the global Gemini Imagen instance
 */

// Example 1: Simple image generation in a Composable
@Composable
fun SimpleImageGenerationExample() {
    var prompt by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Simple Image Generation", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter image description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    imageBitmap = null

                    val response = GeminiImagenApi.generateImage(prompt)

                    isLoading = false
                    if (response.success) {
                        imageBitmap = response.imageData
                    } else {
                        errorMessage = response.errorMessage
                    }
                }
            },
            enabled = prompt.isNotBlank() && !isLoading
        ) {
            Text(if (isLoading) "Generating..." else "Generate Image")
        }

        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error
                )
            }
            imageBitmap != null -> {
                Text("Image generated successfully!")
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Generated image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                )
            }
        }
    }
}

// Example 2: Advanced image generation with parameters
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedImageGenerationExample() {
    var prompt by remember { mutableStateOf("") }
    var aspectRatio by remember { mutableStateOf("1:1") }
    var negativePrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Advanced Image Generation", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        // Aspect ratio selector
        Text("Aspect Ratio")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("1:1", "16:9", "9:16").forEach { ratio ->
                FilterChip(
                    selected = aspectRatio == ratio,
                    onClick = { aspectRatio = ratio },
                    label = { Text(ratio) }
                )
            }
        }

        OutlinedTextField(
            value = negativePrompt,
            onValueChange = { negativePrompt = it },
            label = { Text("Negative Prompt (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    result = null

                    val request = ImageGenerationRequest(
                        prompt = prompt,
                        aspectRatio = aspectRatio,
                        negativePrompt = negativePrompt.ifBlank { null }
                    )

                    val response = GeminiImagenApi.generateImageWithParams(request)

                    isLoading = false
                    result = if (response.success) {
                        "Success! Image data received"
                    } else {
                        "Error: ${response.errorMessage}"
                    }
                }
            },
            enabled = prompt.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Generating..." else "Generate with Parameters")
        }

        result?.let {
            Text(it)
        }
    }
}

// Example 3: Usage in a ViewModel
class ImageGenerationViewModel {
    
    suspend fun generateMonsterImage(description: String): Result<Bitmap> {
        return try {
            val response = GeminiImagenApi.generateImage(
                prompt = "A cute cartoon monster with $description, colorful, friendly, digital art"
            )
            
            if (response.success && response.imageData != null) {
                Result.success(response.imageData)
            } else {
                Result.failure(Exception(response.errorMessage ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generateCustomMonster(
        bodyType: String,
        color: String,
        accessories: String
    ): Result<Bitmap> {
        val prompt = buildString {
            append("A friendly cartoon monster, ")
            append("body type: $bodyType, ")
            append("primary color: $color, ")
            append("wearing: $accessories, ")
            append("cute, colorful, digital art style")
        }
        
        val request = ImageGenerationRequest(
            prompt = prompt,
            aspectRatio = "1:1",
            negativePrompt = "scary, dark, realistic, violent"
        )
        
        return try {
            val response = GeminiImagenApi.generateImageWithParams(request)
            
            if (response.success && response.imageData != null) {
                Result.success(response.imageData)
            } else {
                Result.failure(Exception(response.errorMessage ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Example 4: Utility function for batch generation
object ImageGenerationUtils {
    
    /**
     * Generate multiple images with different prompts
     */
    suspend fun generateBatch(prompts: List<String>): List<Pair<String, Bitmap?>> {
        return prompts.map { prompt ->
            val response = GeminiImagenApi.generateImage(prompt)
            prompt to response.imageData
        }
    }
    
    /**
     * Check if the service is available before attempting generation
     */
    fun checkAvailability(): Boolean {
        return GeminiImagenApi.isReady()
    }
}
