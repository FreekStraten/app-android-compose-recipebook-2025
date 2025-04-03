package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nl.avans.freekstraten.receptenapp.model.Recipe
import nl.avans.freekstraten.receptenapp.network.RecipeApiService

class RecipeRepository {

    private val apiService = RecipeApiService()

    // Function to get recipes as a Flow
    fun getRecipes(): Flow<List<Recipe>> = flow {
        // Emit loading state (empty list)
        emit(emptyList())

        // Fetch from API and emit result
        val recipes = apiService.fetchRecipes()
        emit(recipes)
    }
}