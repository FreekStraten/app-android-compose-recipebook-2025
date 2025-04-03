package nl.avans.freekstraten.receptenapp.util

object Constants {
    // Base URL for The Meal DB API
    const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    // Endpoint for searching by first letter
    const val SEARCH_BY_LETTER = "search.php?f=a"

    // Full URL for getting meals starting with 'a'
    const val MEALS_URL = BASE_URL + SEARCH_BY_LETTER
}