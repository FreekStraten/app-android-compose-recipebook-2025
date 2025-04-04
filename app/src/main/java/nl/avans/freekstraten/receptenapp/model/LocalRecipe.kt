package nl.avans.freekstraten.receptenapp.model

/**
 * Model class for local recipes
 * This is separate from the API model Recipe class
 */
data class LocalRecipe(
    val id: String,
    val name: String,
    val description: String
)