package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBackClick: () -> Unit = {},
    viewModel: RecipeDetailViewModel = viewModel()
) {
    // Load the recipe
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    // Observe the recipe state
    val recipeState = viewModel.recipe.collectAsState()
    val recipe = recipeState.value

    // Create state for the input fields
    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }

    // Update the text fields when recipe changes
    LaunchedEffect(recipe) {
        recipe?.let {
            nameText = it.name
            descriptionText = it.description
        }
    }

    // Track if the form is dirty (has unsaved changes)
    val isDirty = recipe != null && (nameText != recipe.name || descriptionText != recipe.description)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recept Bewerken") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Terug"
                        )
                    }
                },
                actions = {
                    // Only show save button if there are changes
                    if (isDirty) {
                        IconButton(
                            onClick = {
                                viewModel.saveRecipe(nameText, descriptionText)
                                // Optionally navigate back after saving
                                // onBackClick()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Opslaan"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (recipe == null) {
            // Show loading or not found message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Recept wordt geladen of bestaat niet...")
            }
        } else {
            // Show recipe form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Recipe ID text (non-editable)
                Text(
                    text = "Recept ID: ${recipe.id}",
                    style = AppTypography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name input field
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Naam") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description input field
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Beschrijving") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Add a save button at the bottom too
                if (isDirty) {
                    Button(
                        onClick = {
                            viewModel.saveRecipe(nameText, descriptionText)
                            // Show a snackbar or some feedback that changes were saved
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wijzigingen opslaan")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}