package nl.avans.freekstraten.receptenapp.util

import android.content.Context
import nl.avans.freekstraten.receptenapp.repository.LocalRecipeRepository
import nl.avans.freekstraten.receptenapp.repository.OnlineRecipeRepository
import nl.avans.freekstraten.receptenapp.repository.RecipeRepository

object ServiceLocator {
    val localRecipeRepository: RecipeRepository by lazy { LocalRecipeRepository() }
    val onlineRecipeRepository: RecipeRepository by lazy { OnlineRecipeRepository() }

    // Add a property for the PreferencesManager
    private var preferencesManager: PreferencesManager? = null

    /**
     * Initialize the PreferencesManager with the application context
     */
    fun initPreferences(context: Context) {
        if (preferencesManager == null) {
            preferencesManager = PreferencesManager(context.applicationContext)
        }
    }

    /**
     * Get the PreferencesManager instance
     */
    fun getPreferencesManager(): PreferencesManager {
        return preferencesManager ?: throw IllegalStateException(
            "PreferencesManager not initialized. Call initPreferences(context) first."
        )
    }
}