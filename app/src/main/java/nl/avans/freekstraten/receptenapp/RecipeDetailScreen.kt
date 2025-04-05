package nl.avans.freekstraten.receptenapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.util.PermissionHandler
import nl.avans.freekstraten.receptenapp.util.RequestGalleryPermission
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

    // Image URI state
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Context for permission handling
    val context = LocalContext.current
    val permissionHandler = remember { PermissionHandler(context) }

    // Permission state
    var hasPermission by remember { mutableStateOf(permissionHandler.hasGalleryPermission()) }

    // Create image picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Get the selected image URI
            val selectedImageUri = result.data?.data
            imageUri = selectedImageUri
        }
    }

    // Update the text fields when recipe changes
    LaunchedEffect(recipe) {
        recipe?.let {
            nameText = it.name
            descriptionText = it.description
            imageUri = it.imageUri
        }
    }

    // Get focus manager to hide keyboard
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
                                    viewModel.saveRecipe(nameText, descriptionText, imageUri)

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
                // Show image at the top of the screen
                if (recipe.isLocal) {
                    // Image display or placeholder for local recipes
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        if (imageUri != null) {
                            // Display selected image
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Recipe Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (recipe.imageUrl != null) {
                            // Display image from URL
                            AsyncImage(
                                model = recipe.imageUrl,
                                contentDescription = "Recipe Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Placeholder for when no image is available
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Geen afbeelding")
                            }
                        }

                        // Add an image picker button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    // Check permission before launching gallery
                                    if (hasPermission) {
                                        // Create an implicit intent to open the gallery
                                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                        galleryLauncher.launch(intent)
                                    } else {
                                        // Request permission if needed
                                        // Permission result is handled in RequestGalleryPermission below
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else if (recipe.imageUrl != null) {
                    // Display image for online recipes
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                    // Permission request handler
                    RequestGalleryPermission(
                        permissionHandler = permissionHandler,
                        onPermissionResult = { granted ->
                            hasPermission = granted
                            if (granted) {
                                // If permission just granted, open gallery
                                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                galleryLauncher.launch(intent)
                            } else {
                                showToast(context, "Toegang tot de galerij is vereist om een afbeelding te kiezen")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save button for local recipes
                    Button(
                        onClick = {
                            // Hide keyboard
                            focusManager.clearFocus()

                            // Save changes
                            viewModel.saveRecipe(nameText, descriptionText, imageUri)
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