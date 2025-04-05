package nl.avans.freekstraten.receptenapp

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
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

    // Observe the recipe state
    val recipeState = viewModel.recipe.collectAsState()
    val recipe = recipeState.value

    // Create state for the input fields
    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Update the text fields when recipe changes
    LaunchedEffect(recipe) {
        recipe?.let {
            nameText = it.name
            descriptionText = it.description
            imageUri = it.imageUri
        }
    }

    // Get context for showing Toast
    val context = LocalContext.current

    // Focus manager to hide keyboard
    val focusManager = LocalFocusManager.current

    // Create permission handler
    val permissionHandler = remember { PermissionHandler(context) }

    // Show permission request dialog if needed
    var showPermissionRequest by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    // Check permission and launch image picker
    fun launchImagePicker() {
        if (permissionHandler.hasGalleryPermission()) {
            imagePicker.launch("image/*")
        } else {
            showPermissionRequest = true
        }
    }

    // Handle permission request
    if (showPermissionRequest) {
        RequestGalleryPermission(
            permissionHandler = permissionHandler,
            onPermissionResult = { isGranted ->
                showPermissionRequest = false
                if (isGranted) {
                    imagePicker.launch("image/*")
                } else {
                    showToast(context, "Toegang tot galerij is nodig om afbeeldingen toe te voegen")
                }
            }
        )
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
                    // Always show save button
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

                // Image section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { launchImagePicker() },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        // Show selected image
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Recept afbeelding",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (recipe.imageUrl != null) {
                        // Show image from URL
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = "Recept afbeelding",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Show placeholder with icon
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Voeg afbeelding toe",
                                modifier = Modifier.size(48.dp)
                            )
                            Text("Klik om een afbeelding toe te voegen")
                        }
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

                // Always show save button at bottom
                Button(
                    onClick = {
                        // Hide keyboard
                        focusManager.clearFocus()

                        // Save changes
                        viewModel.saveRecipe(nameText, descriptionText, imageUri)

                        // Show toast message
                        showToast(context, "Recept is opgeslagen")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wijzigingen opslaan")
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