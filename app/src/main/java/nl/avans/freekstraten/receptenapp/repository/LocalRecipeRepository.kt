package nl.avans.freekstraten.receptenapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import nl.avans.freekstraten.receptenapp.LocalRecipe

/**
 * Repository for local recipes
 * Maintains separation of concerns by keeping data access logic separate
 * from the UI and ViewModel
 */
class LocalRecipeRepository {
    // In-memory cache of recipes
    private val recipes = listOf(
        LocalRecipe("1", "Pasta Carbonara", "Italiaans gerecht met pasta, ei, kaas en spek"),
        LocalRecipe("2", "Lasagne", "Gelaagd pastagerecht met gehakt en tomatensaus"),
        LocalRecipe("3", "Pizza Margherita", "Traditionele pizza met tomaat, mozzarella en basilicum"),
        LocalRecipe("4", "Tiramisu", "Italiaans dessert met koffie, mascarpone en cacao")
    )

    // Get all local recipes
    fun getRecipes(): Flow<List<LocalRecipe>> = flow {
        emit(recipes)
    }

    // Get a specific recipe by ID
    fun getRecipeById(id: String): Flow<LocalRecipe?> = flow {
        emit(recipes.find { it.id == id })
    }
}