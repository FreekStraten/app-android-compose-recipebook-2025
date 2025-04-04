package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.network.RecipeApiClient
import nl.avans.freekstraten.receptenapp.data.MealDto
import nl.avans.freekstraten.receptenapp.data.toRecipe

class OnlineRecipeRepository : RecipeRepository {

    override fun getRecipes(): Flow<List<Recipe>> = flow {
        emit(emptyList()) // Loading state
        try {
            val response = RecipeApiClient.apiService.getRecipes()
            val recipes = response.meals?.map { it.toRecipe() }?.take(5) ?: emptyList()
            emit(recipes)
        } catch (e: Exception) {
            emit(emptyList()) // Error state
        }
    }

    override fun getRecipeById(id: String): Flow<Recipe?> = flow {
        emit(null) // Not implemented for online recipes
    }

    override fun updateRecipe(recipe: Recipe): Boolean = false // Online recipes can't be updated
}