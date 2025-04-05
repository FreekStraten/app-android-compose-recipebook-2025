package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import nl.avans.freekstraten.receptenapp.data.Recipe
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
            RecipeItem(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun RecipeItem(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Recipe image
        if (recipe.imageUri != null || recipe.imageUrl != null) {
            AsyncImage(
                model = recipe.imageUri ?: recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        // Recipe text information
        Column(
            modifier = Modifier.weight(1f)
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
}