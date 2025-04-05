package nl.avans.freekstraten.receptenapp.navigation

// Define route constants for navigation destinations
object Routes {
    const val MY_RECIPES = "my_recipes"
    const val ONLINE_RECIPES = "online_recipes"
    const val RECIPE_DETAIL = "recipe_detail/{recipeId}"
    const val ONLINE_RECIPE_DETAIL = "online_recipe_detail/{recipeId}"

    // Helper functions to create routes with parameters
    fun recipeDetailRoute(recipeId: String): String {
        return "recipe_detail/$recipeId"
    }

    fun onlineRecipeDetailRoute(recipeId: String): String {
        return "online_recipe_detail/$recipeId"
    }
}