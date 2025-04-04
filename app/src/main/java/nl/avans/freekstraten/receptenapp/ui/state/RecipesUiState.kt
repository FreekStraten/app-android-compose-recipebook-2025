package nl.avans.freekstraten.receptenapp.ui.state

import nl.avans.freekstraten.receptenapp.data.Recipe

// Sealed class to represent all possible UI states
sealed class RecipesUiState {
    data object Loading : RecipesUiState()
    data class Success(val recipes: List<Recipe>) : RecipesUiState()
    data class Error(val message: String) : RecipesUiState()
}