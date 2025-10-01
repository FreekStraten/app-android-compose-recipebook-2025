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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.ui.component.RecipeListItem
import nl.avans.freekstraten.receptenapp.ui.component.SortOrderMenu
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineRecipesScreen(
    viewModel: RecipeViewModel = viewModel(),
    onRecipeClick: (String) -> Unit = {}
) {
    // State uit ViewModel
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRollingDice by viewModel.isRollingDice.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    // Oriëntatie
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Zoek (client-side)
    var query by rememberSaveable { mutableStateOf("") }
    val listToShow by remember(recipes, query) {
        derivedStateOf {
            if (query.isBlank()) recipes
            else recipes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
    }

    // Geen tweede TopAppBar: we tonen alles in content
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header (compact) met acties
            ScreenHeader(
                title = "Online Recepten",
                count = if (query.isBlank()) recipes.size else listToShow.size,
                dense = true
            ) {
                SortOrderMenu(
                    currentSortOrder = sortOrder,
                    onSortOrderSelected = { viewModel.changeSortOrder(it) }
                )

                Spacer(Modifier.width(4.dp))

                // Dobbelsteen: random recipe
                IconButton(
                    onClick = { viewModel.addRandomRecipe() },
                    enabled = !isRollingDice && !isLoading
                ) {
                    if (isRollingDice) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
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

            // Zoekveld
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Zoeken in naam of beschrijving") }
            )

            Spacer(Modifier.height(6.dp))

            when {
                isLoading && recipes.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }

                error != null && recipes.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    if (isLandscape) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(listToShow) { recipe ->
                                RecipeListItem(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            items(listToShow) { recipe ->
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

/** Compacte header (titel + count) met ruimte voor acties (sort/dice). */
@Composable
private fun ScreenHeader(
    title: String,
    count: Int? = null,
    dense: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val vPad = if (dense) 8.dp else 12.dp
    val titleStyle =
        if (dense) MaterialTheme.typography.titleMedium
        else MaterialTheme.typography.titleLarge

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = vPad),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = if (count != null) "$title ($count)" else title
        Text(
            text = text,
            style = titleStyle,
            modifier = Modifier.weight(1f)
        )
        Row(content = actions)
    }
}
