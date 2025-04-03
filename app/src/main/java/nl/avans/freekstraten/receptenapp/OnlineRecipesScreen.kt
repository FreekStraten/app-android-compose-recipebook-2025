package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun OnlineRecipesScreen() {
    val onlineRecipes = remember {
        listOf(
            Recipe("Risotto", "Italiaans rijstgerecht met bouillon en parmezaanse kaas"),
            Recipe("Spaghetti Bolognese", "Pastagerecht met tomatensaus en gehakt"),
            Recipe("Caprese Salade", "Salade met tomaat, mozzarella en basilicum"),
            Recipe("Bruschetta", "Geroosterd brood met tomaat, knoflook en olijfolie")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(onlineRecipes) { recipe ->
            RecipeItem(recipe)
            HorizontalDivider()
        }
    }
}