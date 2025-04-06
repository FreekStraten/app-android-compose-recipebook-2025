package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.repository.OnlineRecipeRepository
import nl.avans.freekstraten.receptenapp.util.PreferencesManager
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class RecipeViewModel : ViewModel() {
    private val onlineRepository = ServiceLocator.onlineRecipeRepository as OnlineRecipeRepository
    private val preferencesManager = ServiceLocator.getPreferencesManager()

    // Just use these three state flows directly - remove the RecipesUiState
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Current sort order for online recipes
    private val _sortOrder = MutableStateFlow(preferencesManager.getOnlineSortOrder())
    val sortOrder: StateFlow<Int> = _sortOrder.asStateFlow()

    // New state for dice rolling animation if needed
    private val _isRollingDice = MutableStateFlow(false)
    val isRollingDice: StateFlow<Boolean> = _isRollingDice.asStateFlow()

    // Initialize by loading recipes
    init {
        loadRecipes()
    }

    // Function to load recipes
    fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                onlineRepository.getRecipes().collect { recipeList ->
                    // Apply sort order before updating the recipes list
                    _recipes.value = sortRecipes(recipeList, _sortOrder.value)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load recipes: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // New function to change sort order
    fun changeSortOrder(newSortOrder: Int) {
        // Save to preferences
        preferencesManager.saveOnlineSortOrder(newSortOrder)
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

    // New function to add a random recipe
    fun addRandomRecipe() {
        viewModelScope.launch {
            _isRollingDice.value = true

            try {
                onlineRepository.getRandomRecipe().collect { randomRecipe ->
                    if (randomRecipe != null) {
                        // Check if recipe already exists in the list to avoid duplicates
                        val recipeExists = _recipes.value.any { it.id == randomRecipe.id }

                        if (!recipeExists) {
                            // Add the new recipe to the current list
                            val updatedList = _recipes.value.toMutableList()
                            updatedList.add(randomRecipe)
                            // Make sure to apply sorting
                            _recipes.value = sortRecipes(updatedList, _sortOrder.value)
                        }
                    }
                    _isRollingDice.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to get random recipe: ${e.message}"
                _isRollingDice.value = false
            }
        }
    }
}