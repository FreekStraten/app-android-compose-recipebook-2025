package nl.avans.freekstraten.receptenapp.util

import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository
import nl.avans.freekstraten.receptenapp.repository.OnlineRecipeRepository
import nl.avans.freekstraten.receptenapp.repository.RecipeRepository

object ServiceLocator {
    val localRecipeRepository: RecipeRepository by lazy { LocalRecipeRepository() }
    val onlineRecipeRepository: RecipeRepository by lazy { OnlineRecipeRepository() }
}