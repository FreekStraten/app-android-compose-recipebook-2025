package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.network.RecipeApiService

class OnlineRecipeRepository : RecipeRepository {
    private val apiService = RecipeApiService()

    // Implementation of interface methods
    override fun getRecipes(): Flow<List<Recipe>> = flow {
        emit(emptyList()) // Loading state
        try {
            val apiRecipes = apiService.fetchRecipes()
            val mappedRecipes = apiRecipes.map { apiRecipe ->
                Recipe(
                    id = apiRecipe.idMeal,
                    name = apiRecipe.strMeal,
                    description = apiRecipe.strInstructions?.take(100)?.plus("...") ?: "No description available",
                    instructions = apiRecipe.strInstructions,
                    imageUrl = apiRecipe.strMealThumb,
                    isLocal = false
                )
            }
            emit(mappedRecipes)
        } catch (e: Exception) {
            emit(emptyList()) // Error state
        }
    }

    override fun getRecipeById(id: String): Flow<Recipe?> = flow {
        // Implement API call to get recipe by ID if available
        // For now, we'll emit null
        emit(null)
    }

    override fun updateRecipe(recipe: Recipe): Boolean {
        // Online recipes can't be updated in this implementation
        return false
    }

    // For backward compatibility (can remove later)
    fun getOnlineRecipes(): Flow<List<Recipe>> = getRecipes()
}