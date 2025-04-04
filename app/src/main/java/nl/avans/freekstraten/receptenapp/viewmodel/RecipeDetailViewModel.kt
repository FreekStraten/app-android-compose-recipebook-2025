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

    // State to track if save operation was successful
    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult

    // Function to load a recipe by ID
    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.getRecipeById(recipeId).collect { loadedRecipe ->
                _recipe.value = loadedRecipe
            }
        }
    }

    // Function to save edited recipe
    fun saveRecipe(name: String, description: String) {
        val currentRecipe = _recipe.value

        if (currentRecipe != null) {
            val updatedRecipe = currentRecipe.copy(
                name = name.trim(),
                description = description.trim()
            )

            viewModelScope.launch {
                val success = repository.updateRecipe(updatedRecipe)
                if (success) {
                    _recipe.value = updatedRecipe
                    _saveResult.value = SaveResult.Success
                } else {
                    _saveResult.value = SaveResult.Error("Kon recept niet opslaan")
                }

                // Reset save result after some time
                kotlinx.coroutines.delay(3000)
                _saveResult.value = null
            }
        } else {
            _saveResult.value = SaveResult.Error("Geen recept geladen om te bewerken")
        }
    }

    // Reset save result (e.g., when navigating away)
    fun resetSaveResult() {
        _saveResult.value = null
    }
}

// Sealed class to represent save operation result
sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}