package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.viewmodel.OnlineRecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineRecipeDetailScreen(
    recipeId: String,
    onBackClick: () -> Unit = {},
    viewModel: OnlineRecipeDetailViewModel = viewModel()
) {
    // Load the recipe
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    // Observe the recipe state
    val recipeState by viewModel.recipe.collectAsState()
    val recipe = recipeState

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
                CircularProgressIndicator()
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
                // Recipe image if available
                recipe.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recipe name
                Text(
                    text = recipe.name,
                    style = AppTypography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Recipe ID
                Text(
                    text = "Recept ID: ${recipe.id}",
                    style = AppTypography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Instructions label
                Text(
                    text = "Bereidingswijze:",
                    style = AppTypography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Full instructions
                Text(
                    text = recipe.instructions ?: recipe.description,
                    style = AppTypography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}