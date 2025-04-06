package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import nl.avans.freekstraten.receptenapp.data.Recipe

/**
 * Interface that defines the contract for recipe data access.
 * Both local and online repositories implement this interface to ensure consistent data operations.
 */
interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe?>
    fun updateRecipe(recipe: Recipe): Boolean
    fun createRecipe(recipe: Recipe): String // Returns the ID of the created recipe
    fun deleteRecipe(id: String): Boolean // New method to delete a recipe
}