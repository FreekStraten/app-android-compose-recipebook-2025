package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.clickable
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
import nl.avans.freekstraten.receptenapp.model.LocalRecipe
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel

@Composable
fun MyRecipesScreen(
    viewModel: MyRecipesViewModel = viewModel(),
    onRecipeClick: (String) -> Unit = {}
) {
    // Collect recipes from the view model
    val recipes by viewModel.recipes.collectAsState()

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