package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import nl.avans.freekstraten.receptenapp.data.Recipe

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe?>
    fun updateRecipe(recipe: Recipe): Boolean
}