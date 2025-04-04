package nl.avans.freekstraten.receptenapp.viewmodel

import androidx.lifecycle.ViewModel
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository

class MyRecipesViewModel(
    // Use the same shared repository instance
    private val repository: LocalRecipeRepository = LocalRecipeRepository()
) : ViewModel() {
    // Expose the recipes StateFlow directly from the repository
    val recipes = repository.recipes
}