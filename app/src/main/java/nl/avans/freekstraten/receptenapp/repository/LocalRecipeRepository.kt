package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nl.avans.freekstraten.receptenapp.data.Recipe

class LocalRecipeRepository : RecipeRepository {
    // In-memory cache of recipes as a MutableStateFlow for reactivity
    private val _localRecipes = MutableStateFlow(
        listOf(
            Recipe(
                id = "1",
                name = "Pasta Carbonara",
                description = "Italiaans gerecht met pasta, ei, kaas en spek",
                isLocal = true
            ),
            Recipe(
                id = "2",
                name = "Lasagne",
                description = "Gelaagd pastagerecht met gehakt en tomatensaus",
                isLocal = true
            ),
            Recipe(
                id = "3",
                name = "Pizza Margherita",
                description = "Traditionele pizza met tomaat, mozzarella en basilicum",
                isLocal = true
            ),
            Recipe(
                id = "4",
                name = "Tiramisu",
                description = "Italiaans dessert met koffie, mascarpone en cacao",
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

    // For backward compatibility (can remove later)
    fun getLocalRecipes(): Flow<List<Recipe>> = getRecipes()
}