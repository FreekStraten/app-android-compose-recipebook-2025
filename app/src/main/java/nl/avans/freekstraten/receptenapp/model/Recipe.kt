package nl.avans.freekstraten.receptenapp.model

data class Recipe(
    val idMeal: String,
    val strMeal: String,
    val strInstructions: String?,
    val strMealThumb: String?
) {
    // For display purposes, use the meal name as display name
    val name: String get() = strMeal

    // A shorter version of instructions for display in lists
    val description: String get() = strInstructions?.take(100)?.plus("...") ?: "No instructions available"
}

// API response wrapper
data class MealResponse(
    val meals: List<Recipe>?
)