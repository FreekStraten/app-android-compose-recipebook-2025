package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import nl.avans.freekstraten.receptenapp.data.Recipe

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe?>
    fun updateRecipe(recipe: Recipe): Boolean
    fun createRecipe(recipe: Recipe): String // Returns the ID of the created recipe
    fun deleteRecipe(id: String): Boolean // New method to delete a recipe
}