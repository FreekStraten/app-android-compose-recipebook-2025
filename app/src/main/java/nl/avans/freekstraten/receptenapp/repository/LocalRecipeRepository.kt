package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nl.avans.freekstraten.receptenapp.data.Recipe
import java.util.UUID

class LocalRecipeRepository : RecipeRepository {
    // In-memory cache of recipes as a MutableStateFlow for reactivity
    private val _localRecipes = MutableStateFlow(
        listOf(
            Recipe(
                id = "1",
                name = "Pasta Carbonara",
                description = "Italiaans gerecht met pasta, ei, kaas en spek",
                imageUrl = "https://www.themealdb.com/images/media/meals/llcbn01574260722.jpg",
                isLocal = true
            ),
            Recipe(
                id = "2",
                name = "Lasagne",
                description = "Gelaagd pastagerecht met gehakt en tomatensaus",
                imageUrl = "https://www.themealdb.com/images/media/meals/wtsvxx1511296896.jpg",
                isLocal = true
            ),
            Recipe(
                id = "3",
                name = "Pizza Margherita",
                description = "Traditionele pizza met tomaat, mozzarella en basilicum",
                imageUrl = "https://www.themealdb.com/images/media/meals/x0lk931587671540.jpg",
                isLocal = true
            ),
            Recipe(
                id = "4",
                name = "Tiramisu",
                description = "Italiaans dessert met koffie, mascarpone en cacao",
                imageUrl = "https://www.themealdb.com/images/media/meals/qvrwpt1511181864.jpg",
                isLocal = true
            )
        )
    )

    // Implementation of interface methods
    override fun getRecipes(): Flow<List<Recipe>> = _localRecipes.asStateFlow()

    override fun getRecipeById(id: String): Flow<Recipe?> = flow {
        val recipe = _localRecipes.value.find { it.id == id }
        emit(recipe)
    }

    override fun updateRecipe(recipe: Recipe): Boolean {
        val currentRecipes = _localRecipes.value.toMutableList()
        val index = currentRecipes.indexOfFirst { it.id == recipe.id }

        if (index != -1) {
            currentRecipes[index] = recipe
            _localRecipes.value = currentRecipes
            return true
        }
        return false
    }

    override fun createRecipe(recipe: Recipe): String {
        // Generate a unique ID for the new recipe
        val newId = UUID.randomUUID().toString()

        // Create a copy of the recipe with the new ID, ensuring isLocal is true
        val newRecipe = recipe.copy(
            id = newId,
            isLocal = true
        )

        // Add the new recipe to the list
        val currentRecipes = _localRecipes.value.toMutableList()
        currentRecipes.add(newRecipe)
        _localRecipes.value = currentRecipes

        return newId
    }

    // For backward compatibility (can remove later)
    fun getLocalRecipes(): Flow<List<Recipe>> = getRecipes()
}