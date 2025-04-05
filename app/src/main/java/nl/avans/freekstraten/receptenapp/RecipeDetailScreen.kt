package nl.avans.freekstraten.receptenapp

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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

    // Observe the recipe and loading states
    val recipeState = viewModel.recipe.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
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

    // Get context for showing Toast
    val context = LocalContext.current

    // Focus manager to hide keyboard
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recept Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Terug"
                        )
                    }
                },
                actions = {
                    // Only show save button for local recipes
                    recipe?.let {
                        if (it.isLocal) {
                            IconButton(
                                onClick = {
                                    // Hide keyboard
                                    focusManager.clearFocus()

                                    // Save changes
                                    viewModel.saveRecipe(nameText, descriptionText)

                                    // Show toast
                                    showToast(context, "Recept is opgeslagen")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Opslaan"
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // Observe save message for other potential uses
        val saveMessage by viewModel.saveMessage.collectAsState()

        // Show toast when save message changes
        LaunchedEffect(saveMessage) {
            if (saveMessage.isNotEmpty()) {
                showToast(context, saveMessage)
                viewModel.clearSaveMessage()
            }
        }

        if (isLoading.value) {
            // Show loading spinner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (recipe == null) {
            // Show not found message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Recept niet gevonden")
            }
        } else {
            // Show recipe details
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

                // For local recipes, show editable fields
                if (recipe.isLocal) {
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

                    // Save button for local recipes
                    Button(
                        onClick = {
                            // Hide keyboard
                            focusManager.clearFocus()

                            // Save changes
                            viewModel.saveRecipe(nameText, descriptionText)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wijzigingen opslaan")
                    }
                } else {
                    // For online recipes, show non-editable details
                    Text(
                        text = "Naam",
                        style = AppTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = recipe.name,
                        style = AppTypography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Beschrijving",
                        style = AppTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = recipe.description,
                        style = AppTypography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Show full instructions if available
                    recipe.instructions?.let { instructions ->
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Volledige instructies",
                            style = AppTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = instructions,
                            style = AppTypography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Helper function to show a toast
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}