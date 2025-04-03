package nl.avans.freekstraten.receptenapp.navigation

// Define route constants for navigation destinations
object Routes {
    const val MY_RECIPES = "my_recipes"
    const val ONLINE_RECIPES = "online_recipes"
    const val RECIPE_DETAIL = "recipe_detail/{recipeId}"

    // Helper function to create route with parameters
    fun recipeDetailRoute(recipeId: String): String {
        return "recipe_detail/$recipeId"
    }
}