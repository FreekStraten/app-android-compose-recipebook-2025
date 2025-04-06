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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.ui.component.RecipeListItem
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(
    viewModel: MyRecipesViewModel = viewModel(),
    onRecipeClick: (String) -> Unit = {},
    onCreateRecipeClick: () -> Unit = {}
) {
    // Collect recipes from the view model
    val recipes by viewModel.recipes.collectAsState()
    val deleteMessage by viewModel.deleteMessage.collectAsState()
    val context = LocalContext.current

    // Show toast when delete message changes
    LaunchedEffect(deleteMessage) {
        deleteMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteMessage()
        }
    }

    // Dialog state for delete confirmation
    var showDeleteDialog by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<String?>(null) }

    // When user confirms deletion
    fun confirmDelete() {
        recipeToDelete?.let { id ->
            viewModel.deleteRecipe(id)
        }
        showDeleteDialog = false
        recipeToDelete = null
    }

    // When user initiates delete
    fun initiateDelete(recipeId: String) {
        recipeToDelete = recipeId
        showDeleteDialog = true
    }

    // Detect screen orientation using Compose's built-in support
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Recept verwijderen") },
            text = { Text("Weet je zeker dat je dit recept wilt verwijderen?") },
            confirmButton = {
                TextButton(onClick = { confirmDelete() }) {
                    Text("Verwijderen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuleren")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mijn Recepten") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateRecipeClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nieuw recept toevoegen"
                )
            }
        }
    ) { paddingValues ->
        if (recipes.isEmpty()) {
            // Show an empty state message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Je hebt nog geen recepten toegevoegd",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (isLandscape) {
            // Use LazyVerticalGrid for landscape orientation
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Show 2 columns in landscape
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(recipes) { recipe ->
                    RecipeListItem(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onDeleteClick = { initiateDelete(recipe.id) }
                    )
                }
            }
        } else {
            // Use LazyColumn for portrait orientation
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp, start = 4.dp, end = 4.dp)
            ) {
                items(recipes) { recipe ->
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