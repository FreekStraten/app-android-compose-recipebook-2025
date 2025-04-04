package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.LocalRecipe
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository

class MyRecipesViewModel : ViewModel() {
    // Repository instance - single source of truth for data
    private val repository = LocalRecipeRepository()

    // State for the recipes list
    private val _recipes = MutableStateFlow<List<LocalRecipe>>(emptyList())
    val recipes: StateFlow<List<LocalRecipe>> = _recipes

    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Set up continuous observation of repository changes
        viewModelScope.launch {
            repository.getRecipes().collect { recipesList ->
                _recipes.value = recipesList
                _isLoading.value = false
            }
        }
    }

    // Can add refresh function if needed
    fun refreshRecipes() {
        _isLoading.value = true
        // Actual refresh happens via collect above
    }
}