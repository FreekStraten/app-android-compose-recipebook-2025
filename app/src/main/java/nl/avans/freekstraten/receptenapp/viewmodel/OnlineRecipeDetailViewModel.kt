package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.repository.RecipeRepository
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class OnlineRecipeDetailViewModel : ViewModel() {
    // Use the online repository
    private val repository: RecipeRepository = ServiceLocator.onlineRecipeRepository

    // State for the current recipe
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    // Function to load a recipe by ID
    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.getRecipeById(recipeId).collect { loadedRecipe ->
                _recipe.value = loadedRecipe
            }
        }
    }
}