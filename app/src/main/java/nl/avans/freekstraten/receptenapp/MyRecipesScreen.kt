package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography

// Import the local Recipe data class for local recipes
// This is separate from the API model
data class LocalRecipe(val name: String, val description: String)

@Composable
fun MyRecipesScreen() {
    val recipes = remember {
        listOf(
            LocalRecipe("Pasta Carbonara", "Italiaans gerecht met pasta, ei, kaas en spek"),
            LocalRecipe("Lasagne", "Gelaagd pastagerecht met gehakt en tomatensaus"),
            LocalRecipe("Pizza Margherita", "Traditionele pizza met tomaat, mozzarella en basilicum"),
            LocalRecipe("Tiramisu", "Italiaans dessert met koffie, mascarpone en cacao")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(recipes) { recipe ->
            LocalRecipeItem(recipe)
            HorizontalDivider()
        }
    }
}

@Composable
fun LocalRecipeItem(recipe: LocalRecipe) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = recipe.name,
            style = AppTypography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = recipe.description,
            style = AppTypography.bodyMedium
        )
    }
}