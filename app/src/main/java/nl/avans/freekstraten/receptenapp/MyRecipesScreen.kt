package nl.avans.freekstraten.receptenapp

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.ui.component.RecipeListItem
import nl.avans.freekstraten.receptenapp.ui.component.SortOrderMenu
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(
    viewModel: MyRecipesViewModel = viewModel(),
    onRecipeClick: (String) -> Unit = {},
    onCreateRecipeClick: () -> Unit = {}
) {
    // Data
    val recipes by viewModel.recipes.collectAsState()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val context = LocalContext.current

    // Toast op delete
    LaunchedEffect(deleteMessage) {
        deleteMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteMessage()
        }
    }

    // Delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<String?>(null) }
    fun confirmDelete() {
        recipeToDelete?.let { viewModel.deleteRecipe(it) }
        showDeleteDialog = false
        recipeToDelete = null
    }
    fun initiateDelete(recipeId: String) {
        recipeToDelete = recipeId
        showDeleteDialog = true
    }

    // Orientation
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Zoekveld (client-side filter)
    var query by rememberSaveable { mutableStateOf("") }
    val listToShow by remember(recipes, query) {
        mutableStateOf(
            if (query.isBlank()) recipes
            else recipes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        )
    }

    // Delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Recept verwijderen") },
            text = { Text("Weet je zeker dat je dit recept wilt verwijderen?") },
            confirmButton = { TextButton(onClick = { confirmDelete() }) { Text("Verwijderen") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Annuleren") } }
        )
    }

    // Scaffold zónder extra TopAppBar (MealMate staat al boven)
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateRecipeClick,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nieuw recept toevoegen") },
                text = { Text("Nieuw recept") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sectietitel + sort
            ScreenHeader(
                title = "Mijn Recepten",
                count = recipes.size,     // toont bv. "Mijn Recepten (4)"
                dense = true
            ) {
                SortOrderMenu(
                    currentSortOrder = sortOrder,
                    onSortOrderSelected = { viewModel.changeSortOrder(it) }
                )
            }


            // Zoekveld
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),   // laat top-padding weg
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Zoeken in naam of beschrijving") }
            )
            Spacer(Modifier.height(6.dp))

            if (listToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (recipes.isEmpty()) "Je hebt nog geen recepten toegevoegd"
                        else "Geen resultaten voor “$query”",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (isLandscape) {
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
                            onClick = { onRecipeClick(recipe.id) },
                            onDeleteClick = { initiateDelete(recipe.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp, start = 4.dp, end = 4.dp)
                ) {
                    items(listToShow) { recipe ->
                        RecipeListItem(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            onDeleteClick = { initiateDelete(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    count: Int? = null,
    dense: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val vPad = if (dense) 8.dp else 12.dp
    val titleStyle = if (dense) MaterialTheme.typography.titleMedium
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

