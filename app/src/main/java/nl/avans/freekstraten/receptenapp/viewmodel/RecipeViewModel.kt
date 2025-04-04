package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.ui.state.RecipesUiState
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class RecipeViewModel : ViewModel() {
    private val onlineRepository = ServiceLocator.onlineRecipeRepository

    // Single StateFlow for the entire UI state
    private val _uiState = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    // Initialize by loading recipes
    init {
        loadRecipes()
    }

    // Function to load recipes
    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipesUiState.Loading

            try {
                onlineRepository.getRecipes().collect { recipeList ->
                    _uiState.value = RecipesUiState.Success(recipeList)
                }
            } catch (e: Exception) {
                _uiState.value = RecipesUiState.Error("Failed to load recipes: ${e.message}")
            }
        }
    }
}