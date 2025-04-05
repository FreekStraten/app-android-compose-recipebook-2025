package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository
import nl.avans.freekstraten.receptenapp.repository.RecipeRepository
import nl.avans.freekstraten.receptenapp.util.ServiceLocator

class MyRecipesViewModel(
    // Use the shared repository instance
    private val repository: RecipeRepository = ServiceLocator.localRecipeRepository
) : ViewModel() {
    // State for recipes
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.getRecipes().collect { recipeList ->
                _recipes.value = recipeList
            }
        }
    }

    // New function to create a recipe
    fun createNewRecipe(recipe: Recipe): String {
        // Add it to the repository
        return repository.createRecipe(recipe)
    }

    // Overloaded function for backward compatibility
    fun createNewRecipe(name: String, description: String): String {
        // Create a new recipe with default values
        val newRecipe = Recipe(
            id = "", // Will be replaced by the repository
            name = name,
            description = description,
            isLocal = true
        )

        // Add it to the repository
        return repository.createRecipe(newRecipe)
    }
}