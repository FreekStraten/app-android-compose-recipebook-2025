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

@Composable
fun MyRecipesScreen() {
    val recipes = remember {
        listOf(
            Recipe("Pasta Carbonara", "Italiaans gerecht met pasta, ei, kaas en spek"),
            Recipe("Lasagne", "Gelaagd pastagerecht met gehakt en tomatensaus"),
            Recipe("Pizza Margherita", "Traditionele pizza met tomaat, mozzarella en basilicum"),
            Recipe("Tiramisu", "Italiaans dessert met koffie, mascarpone en cacao")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(recipes) { recipe ->
            RecipeItem(recipe)
            Divider()
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
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