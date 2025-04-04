package nl.avans.freekstraten.receptenapp.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.util.Constants
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RecipeApiService {

    // Function to fetch recipes from the API
    suspend fun fetchRecipes(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(Constants.MEALS_URL).openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    parseRecipesResponse(response)
                } else {
                    Log.e("RecipeApiService", "Error fetching recipes: $responseCode")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("RecipeApiService", "Exception fetching recipes", e)
                emptyList()
            }
        }
    }

    // Helper function to parse the JSON response
    private fun parseRecipesResponse(jsonString: String): List<Recipe> {
        return try {
            val jsonObject = JSONObject(jsonString)
            val mealsArray = jsonObject.optJSONArray("meals") ?: return emptyList()

            (0 until mealsArray.length()).map { i ->
                val meal = mealsArray.getJSONObject(i)
                val instructions = meal.optString("strInstructions", "")

                Recipe(
                    id = meal.optString("idMeal", ""),
                    name = meal.optString("strMeal", "Unknown Recipe"),
                    description = instructions.take(100).let { if (instructions.length > 100) "$it..." else it },
                    instructions = instructions,
                    imageUrl = meal.optString("strMealThumb", ""),
                    isLocal = false
                )
            }.take(5)
        } catch (e: Exception) {
            Log.e("RecipeApiService", "Error parsing recipes", e)
            emptyList()
        }
    }
}