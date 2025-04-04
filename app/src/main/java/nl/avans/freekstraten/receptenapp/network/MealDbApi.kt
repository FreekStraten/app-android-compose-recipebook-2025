package nl.avans.freekstraten.receptenapp.network

import retrofit2.http.GET
import nl.avans.freekstraten.receptenapp.data.MealResponse

interface MealDbApi {
    @GET("search.php?f=a")
    suspend fun getRecipes(): MealResponse

    @GET("random.php")
    suspend fun getRandomRecipe(): MealResponse
}