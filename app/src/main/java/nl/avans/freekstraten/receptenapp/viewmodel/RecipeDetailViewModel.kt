package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.model.LocalRecipe
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository

class RecipeDetailViewModel(
    // Use a shared repository instance
    private val repository: LocalRecipeRepository = LocalRecipeRepository()
) : ViewModel() {
    // State for the current recipe
    private val _recipe = MutableStateFlow<LocalRecipe?>(null)
    val recipe: StateFlow<LocalRecipe?> = _recipe.asStateFlow()

    // State for save message notification
    private val _saveMessage = MutableStateFlow("")
    val saveMessage: StateFlow<String> = _saveMessage.asStateFlow()

    // Function to load a recipe by ID
    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.getRecipeById(recipeId).collect { loadedRecipe ->
                _recipe.value = loadedRecipe
            }
        }
    }

    // Function to save changes to a recipe
    fun saveRecipe(name: String, description: String) {
        val currentRecipe = _recipe.value ?: return

        // Create an updated copy of the recipe
        val updatedRecipe = LocalRecipe(
            id = currentRecipe.id,
            name = name,
            description = description
        )

        // Update in repository
        repository.updateRecipe(updatedRecipe)

        // Update local state
        _recipe.value = updatedRecipe
    }

    // Function to show saved message
    fun showSavedMessage() {
        _saveMessage.value = "Recept is opgeslagen"
    }

    // Function to clear save message
    fun clearSaveMessage() {
        _saveMessage.value = ""
    }
}