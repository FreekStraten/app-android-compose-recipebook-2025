package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.model.Recipe as ApiRecipe
import nl.avans.freekstraten.receptenapp.network.RecipeApiService

class OnlineRecipeRepository : RecipeRepository {
    private val apiService = RecipeApiService()

    // Convert API Recipe to unified Recipe model
    private fun ApiRecipe.toRecipe(): Recipe {
        return Recipe(
            id = this.idMeal,
            name = this.strMeal,
            description = this.strInstructions?.take(100)?.plus("...") ?: "No description available",
            instructions = this.strInstructions,
            imageUrl = this.strMealThumb,
            isLocal = false
        )
    }

    // Implementation of interface methods
    override fun getRecipes(): Flow<List<Recipe>> = flow {
        emit(emptyList()) // Loading state
        try {
            val apiRecipes = apiService.fetchRecipes()
            emit(apiRecipes.map { it.toRecipe() })
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