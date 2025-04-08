package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository
import nl.avans.freekstraten.receptenapp.repository.RecipeRepository
import nl.avans.freekstraten.receptenapp.util.PreferencesManager
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class MyRecipesViewModel(
    // Use the shared repository instance
    private val repository: RecipeRepository = ServiceLocator.localRecipeRepository
) : ViewModel() {

    private val preferencesManager = ServiceLocator.getPreferencesManager()

    // State for recipes
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    // State for delete operation feedback
    private val _deleteMessage = MutableStateFlow<String?>(null)
    val deleteMessage: StateFlow<String?> = _deleteMessage.asStateFlow()

    // Current sort order for local recipes
    private val _sortOrder = MutableStateFlow(preferencesManager.getLocalSortOrder())
    val sortOrder: StateFlow<Int> = _sortOrder.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.getRecipes().collect { recipeList ->
                // Apply sort order before updating the recipes list
                _recipes.value = sortRecipes(recipeList, _sortOrder.value)
            }
        }
    }

    // New function to change sort order
    fun changeSortOrder(newSortOrder: Int) {
        // Save to preferences
        preferencesManager.saveLocalSortOrder(newSortOrder)
        _sortOrder.value = newSortOrder

        // Sort the existing recipes with the new order
        _recipes.value = sortRecipes(_recipes.value, newSortOrder)
    }

    // Helper function to sort recipes according to sort order
    private fun sortRecipes(recipes: List<Recipe>, sortOrder: Int): List<Recipe> {
        return when (sortOrder) {
            PreferencesManager.SORT_A_Z -> recipes.sortedBy { it.name }
            PreferencesManager.SORT_Z_A -> recipes.sortedByDescending { it.name }
            else -> recipes // No sorting (use original order)
        }
    }

    // New function to create a recipe
    fun createNewRecipe(recipe: Recipe): String {
        // Add it to the repository
        return repository.createRecipe(recipe)
    }

    // Overloaded function for backward compatibility
    fun createNewRecipe(name: String, description: String): String {
        // Create a new recipe with default values
        val newRecipe = Recipe(
            id = "", // Will be replaced by the repository
            name = name,
            description = description,
            isLocal = true
        )

        // Add it to the repository
        return repository.createRecipe(newRecipe)
    }

    // New function to delete a recipe
    fun deleteRecipe(recipeId: String): Boolean {
        val result = repository.deleteRecipe(recipeId)

        if (result) {
            _deleteMessage.value = "Recept is verwijderd"
        } else {
            _deleteMessage.value = "Kon het recept niet verwijderen"
        }

        return result
    }

    // Function to clear delete message
    fun clearDeleteMessage() {
        _deleteMessage.value = null
    }
}