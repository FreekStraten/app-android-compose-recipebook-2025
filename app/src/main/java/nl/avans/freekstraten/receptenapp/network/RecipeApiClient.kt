package nl.avans.freekstraten.receptenapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import nl.avans.freekstraten.receptenapp.util.Constants

object RecipeApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: MealDbApi = retrofit.create(MealDbApi::class.java)
}