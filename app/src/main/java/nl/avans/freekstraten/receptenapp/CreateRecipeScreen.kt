package nl.avans.freekstraten.receptenapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import nl.avans.freekstraten.receptenapp.data.Recipe
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
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    onBackClick: () -> Unit = {},
    onRecipeCreated: (String) -> Unit = {},
    viewModel: MyRecipesViewModel = viewModel()
) {
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

    // Create permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            // Launch gallery if permission is granted
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        } else {
            showToast(context, "Toegang tot de galerij is vereist om een afbeelding te kiezen")
        }
    }

    // Get focus manager to hide keyboard
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nieuw Recept") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Terug"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Only proceed if there's a name
                            if (nameText.isBlank()) {
                                showToast(context, "Voer een naam in voor het recept")
                                return@IconButton
                            }

                            // Hide keyboard
                            focusManager.clearFocus()

                            // Create new recipe and get its ID
                            val newRecipeId = viewModel.createNewRecipe(nameText, descriptionText)

                            // Show toast
                            showToast(context, "Recept is aangemaakt")

                            // Navigate to the detail screen of the new recipe
                            onRecipeCreated(newRecipeId)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Opslaan"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Show recipe creation form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Image selector at the top of the screen
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
                                permissionLauncher.launch(permissionHandler.getRequiredPermission())
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

            // Create button
            Button(
                onClick = {
                    // Only proceed if there's a name
                    if (nameText.isBlank()) {
                        showToast(context, "Voer een naam in voor het recept")
                        return@Button
                    }

                    // Hide keyboard
                    focusManager.clearFocus()

                    // Create new recipe and get its ID
                    val newRecipe = Recipe(
                        id = "", // Will be replaced by repository
                        name = nameText,
                        description = descriptionText,
                        imageUri = imageUri,
                        isLocal = true
                    )

                    val newRecipeId = viewModel.createNewRecipe(newRecipe)

                    // Show toast
                    showToast(context, "Recept is aangemaakt")

                    // Navigate to the detail screen of the new recipe
                    onRecipeCreated(newRecipeId)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recept aanmaken")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Helper function to show a toast
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}