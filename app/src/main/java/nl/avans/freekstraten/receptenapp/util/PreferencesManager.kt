package nl.avans.freekstraten.receptenapp.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * Manages saving and retrieving user preferences
 */
class PreferencesManager(context: Context) {

    companion object {
        private const val PREF_NAME = "RecipeAppPrefs"
        private const val KEY_ONLINE_SORT_ORDER = "online_sort_order"
        private const val KEY_LOCAL_SORT_ORDER = "local_sort_order"

        // Sort order constants
        const val SORT_NONE = 0
        const val SORT_A_Z = 1
        const val SORT_Z_A = 2
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Get the current sort order for online recipes
     * @return Sort order value (SORT_NONE, SORT_A_Z, or SORT_Z_A)
     */
    fun getOnlineSortOrder(): Int {
        return sharedPreferences.getInt(KEY_ONLINE_SORT_ORDER, SORT_NONE)
    }

    /**
     * Save the sort order for online recipes
     * @param sortOrder The sort order to save
     */
    fun saveOnlineSortOrder(sortOrder: Int) {
        sharedPreferences.edit().putInt(KEY_ONLINE_SORT_ORDER, sortOrder).apply()
    }

    /**
     * Get the current sort order for local recipes
     * @return Sort order value (SORT_NONE, SORT_A_Z, or SORT_Z_A)
     */
    fun getLocalSortOrder(): Int {
        return sharedPreferences.getInt(KEY_LOCAL_SORT_ORDER, SORT_NONE)
    }

    /**
     * Save the sort order for local recipes
     * @param sortOrder The sort order to save
     */
    fun saveLocalSortOrder(sortOrder: Int) {
        sharedPreferences.edit().putInt(KEY_LOCAL_SORT_ORDER, sortOrder).apply()
    }
}