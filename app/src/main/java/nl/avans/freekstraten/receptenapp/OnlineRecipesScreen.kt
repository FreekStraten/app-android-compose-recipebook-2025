package nl.avans.freekstraten.receptenapp

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.ui.component.RecipeListItem
import nl.avans.freekstraten.receptenapp.ui.component.SortOrderMenu
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineRecipesScreen(
    viewModel: RecipeViewModel = viewModel(),
    onRecipeClick: (String) -> Unit = {}
) {
    // Collect state from ViewModel
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRollingDice by viewModel.isRollingDice.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    // Detect screen orientation using Compose's built-in support
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Online Recepten") },
                actions = {
                    // Add sort menu to the app bar
                    SortOrderMenu(
                        currentSortOrder = sortOrder,
                        onSortOrderSelected = { newSortOrder ->
                            viewModel.changeSortOrder(newSortOrder)
                        }
                    )

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
                // Show recipes - use grid in landscape, list in portrait
                else -> {
                    if (isLandscape) {
                        // Use LazyVerticalGrid for landscape orientation
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2), // Show 2 columns in landscape
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipes) { recipe ->
                                RecipeListItem(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    } else {
                        // Use LazyColumn for portrait orientation
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            items(recipes) { recipe ->
                                RecipeListItem(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}