package nl.avans.freekstraten.receptenapp.data

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val instructions: String? = null,
    val imageUrl: String? = null,
    val isLocal: Boolean = true
)