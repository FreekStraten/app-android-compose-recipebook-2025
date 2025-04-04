package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class RecipeViewModel : ViewModel() {
    private val onlineRepository = ServiceLocator.onlineRecipeRepository

    // Just use these three state flows directly - remove the RecipesUiState
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
                    _recipes.value = recipeList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load recipes: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}