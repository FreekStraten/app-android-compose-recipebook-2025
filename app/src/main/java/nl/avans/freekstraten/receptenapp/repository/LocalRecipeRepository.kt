package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import nl.avans.freekstraten.receptenapp.LocalRecipe

/**
 * Repository for local recipes
 * Maintains separation of concerns by keeping data access logic separate
 * from the UI and ViewModel
 */
class LocalRecipeRepository {
    // In-memory cache of recipes in a mutable list to allow editing
    private val _recipes = MutableStateFlow(mutableListOf(
        LocalRecipe("1", "Pasta Carbonara", "Italiaans gerecht met pasta, ei, kaas en spek"),
        LocalRecipe("2", "Lasagne", "Gelaagd pastagerecht met gehakt en tomatensaus"),
        LocalRecipe("3", "Pizza Margherita", "Traditionele pizza met tomaat, mozzarella en basilicum"),
        LocalRecipe("4", "Tiramisu", "Italiaans dessert met koffie, mascarpone en cacao")
    ))

    // Public immutable view of recipes
    val recipesFlow = _recipes.asStateFlow()

    // Get all local recipes
    fun getRecipes(): Flow<List<LocalRecipe>> = recipesFlow

    // Get a specific recipe by ID
    fun getRecipeById(id: String): Flow<LocalRecipe?> = flow {
        emit(_recipes.value.find { it.id == id })
    }

    // Update an existing recipe
    fun updateRecipe(updatedRecipe: LocalRecipe): Boolean {
        val currentRecipes = _recipes.value
        val index = currentRecipes.indexOfFirst { it.id == updatedRecipe.id }

        if (index != -1) {
            // Create a new list with the updated recipe
            val newList = currentRecipes.toMutableList()
            newList[index] = updatedRecipe
            _recipes.value = newList
            return true
        }
        return false
    }
}