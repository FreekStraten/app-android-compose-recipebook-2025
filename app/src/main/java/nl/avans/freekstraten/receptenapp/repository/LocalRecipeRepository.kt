package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nl.avans.freekstraten.receptenapp.model.LocalRecipe

/**
 * Repository for local recipes
 * Maintains separation of concerns by keeping data access logic separate
 * from the UI and ViewModel
 */
class LocalRecipeRepository {
    // In-memory cache of recipes as a MutableStateFlow for reactivity
    private val _recipes = MutableStateFlow(
        listOf(
            LocalRecipe("1", "Pasta Carbonara", "Italiaans gerecht met pasta, ei, kaas en spek"),
            LocalRecipe("2", "Lasagne", "Gelaagd pastagerecht met gehakt en tomatensaus"),
            LocalRecipe("3", "Pizza Margherita", "Traditionele pizza met tomaat, mozzarella en basilicum"),
            LocalRecipe("4", "Tiramisu", "Italiaans dessert met koffie, mascarpone en cacao")
        )
    )

    // Expose recipes as StateFlow (immutable to outside classes)
    val recipes: StateFlow<List<LocalRecipe>> = _recipes.asStateFlow()

    // Get a specific recipe by ID
    fun getRecipeById(id: String): Flow<LocalRecipe?> = flow {
        val recipe = _recipes.value.find { it.id == id }
        emit(recipe)
    }

    // Update a recipe
    fun updateRecipe(updatedRecipe: LocalRecipe) {
        val currentRecipes = _recipes.value.toMutableList()
        val index = currentRecipes.indexOfFirst { it.id == updatedRecipe.id }

        if (index != -1) {
            currentRecipes[index] = updatedRecipe
            _recipes.value = currentRecipes
        }
    }
}