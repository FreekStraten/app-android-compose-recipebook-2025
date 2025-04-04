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
            // Recipe class is now unified, so we don't need to map between different models
            val recipes = apiService.fetchRecipes()
            emit(recipes)
        } catch (e: Exception) {
            emit(emptyList()) // Error state
        }
    }

    override fun getRecipeById(id: String): Flow<Recipe?> = flow {
        // We could implement an API call to get recipe by ID if needed
        // For this example, we'll just emit null
        emit(null)
    }

    override fun updateRecipe(recipe: Recipe): Boolean {
        // Online recipes can't be updated in this implementation
        return false
    }

    // For backward compatibility (can remove later)
    fun getOnlineRecipes(): Flow<List<Recipe>> = getRecipes()
}