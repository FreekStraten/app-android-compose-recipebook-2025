package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeDetailViewModel
import nl.avans.freekstraten.receptenapp.viewmodel.SaveResult

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
    val recipe by viewModel.recipe.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()

    // Create state for the input fields
    // Default to empty strings if recipe is null
    var nameText by remember(recipe) { mutableStateOf(recipe?.name ?: "") }
    var descriptionText by remember(recipe) { mutableStateOf(recipe?.description ?: "") }

    // State for showing snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar when save result changes
    LaunchedEffect(saveResult) {
        saveResult?.let {
            when (it) {
                is SaveResult.Success -> {
                    snackbarHostState.showSnackbar("Recept opgeslagen!")
                }
                is SaveResult.Error -> {
                    snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    // Reset save result when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSaveResult()
        }
    }

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
                    // Save button
                    IconButton(
                        onClick = {
                            viewModel.saveRecipe(nameText, descriptionText)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Opslaan"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                CircularProgressIndicator()
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
                    text = "Recept ID: ${recipe?.id}",
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

                // Save button
                Button(
                    onClick = { viewModel.saveRecipe(nameText, descriptionText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Opslaan")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}