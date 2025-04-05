package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.repository.RecipeRepository
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class RecipeDetailViewModel : ViewModel() {
    // State for the current recipe
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    // State for loading indicator
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for save message notification
    private val _saveMessage = MutableStateFlow("")
    val saveMessage: StateFlow<String> = _saveMessage.asStateFlow()

    // Function to load a recipe by ID
    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // First try local repository
            val localRecipe = ServiceLocator.localRecipeRepository.getRecipeById(recipeId).firstOrNull()

            if (localRecipe != null) {
                _recipe.value = localRecipe
                _isLoading.value = false
                return@launch
            }

            // If not found locally, try online repository
            val onlineRecipe = ServiceLocator.onlineRecipeRepository.getRecipeById(recipeId).firstOrNull()

            if (onlineRecipe != null) {
                _recipe.value = onlineRecipe
                _isLoading.value = false
                return@launch
            }

            // If recipe is not found in either repository
            _isLoading.value = false
        }
    }

    // Function to save changes to a recipe
    fun saveRecipe(name: String, description: String) {
        val currentRecipe = _recipe.value ?: return

        // Only allow saving local recipes
        if (!currentRecipe.isLocal) {
            _saveMessage.value = "Online recepten kunnen niet worden bewerkt"
            return
        }

        // Create an updated copy of the recipe
        val updatedRecipe = currentRecipe.copy(
            name = name,
            description = description
        )

        // Update in repository
        val success = ServiceLocator.localRecipeRepository.updateRecipe(updatedRecipe)

        if (success) {
            // Update local state
            _recipe.value = updatedRecipe
            _saveMessage.value = "Recept is opgeslagen"
        } else {
            _saveMessage.value = "Fout bij opslaan van recept"
        }
    }

    // Function to clear save message
    fun clearSaveMessage() {
        _saveMessage.value = ""
    }
}