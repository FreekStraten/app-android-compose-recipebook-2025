package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.LocalRecipe
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository

class RecipeDetailViewModel : ViewModel() {
    // Repository instance - single source of truth for data
    private val repository = LocalRecipeRepository()

    // State for the current recipe
    private val _recipe = MutableStateFlow<LocalRecipe?>(null)
    val recipe: StateFlow<LocalRecipe?> = _recipe

    // Function to load a recipe by ID
    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.getRecipeById(recipeId).collect { loadedRecipe ->
                _recipe.value = loadedRecipe
            }
        }
    }
}