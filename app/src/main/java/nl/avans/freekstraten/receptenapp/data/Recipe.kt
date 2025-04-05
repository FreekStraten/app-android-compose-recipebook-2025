package nl.avans.freekstraten.receptenapp.data

import android.net.Uri

/**
 * Unified Recipe model for both local and online recipes
 */
data class Recipe(
    val id: String,                   // maps to idMeal for online recipes
    val name: String,                 // maps to strMeal for online recipes
    val description: String,          // shortened version of instructions or description
    val instructions: String? = null, // full instructions text, maps to strInstructions for online
    val imageUrl: String? = null,     // maps to strMealThumb for online recipes
    val imageUri: Uri? = null,        // local image Uri for local recipes
    val isLocal: Boolean = true       // flag to differentiate local vs online recipes
)