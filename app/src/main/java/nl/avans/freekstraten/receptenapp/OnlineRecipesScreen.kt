package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.model.Recipe
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeViewModel
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography

@Composable
fun OnlineRecipesScreen(
    viewModel: RecipeViewModel = viewModel()
) {
    // Collect state from ViewModel
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Simply use LazyColumn as the root composable, like in MyRecipesScreen
    when {
        // Show loading spinner if loading
        isLoading && recipes.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // Show error if there is one
        error != null && recipes.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        // Show recipes if available
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recipes) { recipe ->
                    OnlineRecipeItem(recipe)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun OnlineRecipeItem(recipe: Recipe) {
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