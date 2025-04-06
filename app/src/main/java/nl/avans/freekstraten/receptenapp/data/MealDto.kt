package nl.avans.freekstraten.receptenapp.data

import nl.avans.freekstraten.receptenapp.data.Recipe

data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strInstructions: String?,
    val strMealThumb: String?
)

/**
 * Extension function to convert API-specific DTO to domain model.
 * This creates a clean separation between external and internal data models.
 */
fun MealDto.toRecipe(): Recipe {
    val shortenedDescription = strInstructions?.take(100)?.let {
        if (strInstructions.length > 100) "$it..." else it
    } ?: ""

    return Recipe(
        id = idMeal,
        name = strMeal,
        description = shortenedDescription,
        instructions = strInstructions,
        imageUrl = strMealThumb,
        imageUri = null,
        isLocal = false
    )
}