package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
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
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeViewModel
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineRecipesScreen(
    viewModel: RecipeViewModel = viewModel()
) {
    // Collect state from ViewModel
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRollingDice by viewModel.isRollingDice.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Online Recepten") },
                actions = {
                    // Dice button for adding random recipe
                    IconButton(
                        onClick = { viewModel.addRandomRecipe() },
                        enabled = !isRollingDice && !isLoading
                    ) {
                        if (isRollingDice) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Random recept toevoegen"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                // Show loading spinner if loading
                isLoading && recipes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // Show error if there is one
                error != null && recipes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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
    }
}

@Composable
fun OnlineRecipeItem(recipe: Recipe) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Recipe image
        if (recipe.imageUrl != null) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(120.dp)
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