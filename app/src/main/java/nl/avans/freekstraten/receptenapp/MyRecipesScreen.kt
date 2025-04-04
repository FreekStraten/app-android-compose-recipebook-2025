package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel

// Import the local Recipe data class for local recipes
// This is separate from the API model
data class LocalRecipe(val id: String, val name: String, val description: String)

@Composable
fun MyRecipesScreen(
    onRecipeClick: (String) -> Unit = {},
    viewModel: MyRecipesViewModel = viewModel()
) {
    // Load recipes from the repository via the ViewModel
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(recipes) { recipe ->
                LocalRecipeItem(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun LocalRecipeItem(
    recipe: LocalRecipe,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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