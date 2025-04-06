package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.network.RecipeApiClient
import nl.avans.freekstraten.receptenapp.data.MealDto
import nl.avans.freekstraten.receptenapp.data.toRecipe

class OnlineRecipeRepository : RecipeRepository {
    // Cache for recipes we've already fetched
    private val cachedRecipes = MutableStateFlow<Map<String, Recipe>>(emptyMap())

    override fun getRecipes(): Flow<List<Recipe>> = flow {
        emit(emptyList()) // Loading state
        try {
            val response = RecipeApiClient.apiService.getRecipes()
            val recipes = response.meals?.map { it.toRecipe() } ?: emptyList()

            // Update the cache
            val recipeMap = recipes.associateBy { it.id }
            cachedRecipes.value = recipeMap

            emit(recipes)
        } catch (e: Exception) {
            emit(emptyList()) // Error state
        }
    }

    override fun getRecipeById(id: String): Flow<Recipe?> = flow {
        // First check if we have it in our cache
        val cachedRecipe = cachedRecipes.value[id]
        if (cachedRecipe != null) {
            emit(cachedRecipe)
            return@flow
        }

        // If not in cache, return null since we don't have an API endpoint to fetch by ID
        emit(null)
    }

    override fun updateRecipe(recipe: Recipe): Boolean = false // Online recipes can't be updated

    // Method to get a random recipe
    fun getRandomRecipe(): Flow<Recipe?> = flow {
        emit(null) // Loading state
        try {
            val response = RecipeApiClient.apiService.getRandomRecipe()
            val recipe = response.meals?.firstOrNull()?.toRecipe()

            // Update cache with this recipe too
            recipe?.let {
                val updated = cachedRecipes.value.toMutableMap()
                updated[it.id] = it
                cachedRecipes.value = updated
            }

            emit(recipe)
        } catch (e: Exception) {
            emit(null) // Error state
        }
    }

    override fun createRecipe(recipe: Recipe): String {
        // Online repository doesn't support creating recipes
        // This is just here to satisfy the interface
        throw UnsupportedOperationException("Cannot create recipes in online repository")
    }

    // Implementation for deleting a recipe - not supported for online recipes
    override fun deleteRecipe(id: String): Boolean {
        // Online recipes can't be deleted
        return false
    }
}