package nl.avans.freekstraten.receptenapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBackClick: () -> Unit = {},
    viewModel: RecipeDetailViewModel = viewModel()
) {
    // Load & observe
    LaunchedEffect(recipeId) { viewModel.loadRecipe(recipeId) }
    val recipe by viewModel.recipe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Editable state
    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Context & permissions
    val context = LocalContext.current
    val permissionHandler = remember { PermissionHandler(context) }
    var hasPermission by remember { mutableStateOf(permissionHandler.hasGalleryPermission()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            imageUri = result.data?.data
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        } else showToast(context, "Toegang tot de galerij is vereist om een afbeelding te kiezen")
    }

    // Sync UI fields with recipe
    LaunchedEffect(recipe) {
        recipe?.let {
            nameText = it.name
            descriptionText = it.description
            imageUri = it.imageUri
        }
    }

    val focus = LocalFocusManager.current
    val saveMessage by viewModel.saveMessage.collectAsState()
    LaunchedEffect(saveMessage) {
        if (saveMessage.isNotEmpty()) {
            showToast(context, saveMessage)
            viewModel.clearSaveMessage()
        }
    }

    Scaffold { inner ->
        when {
            isLoading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            recipe == null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) { Text("Recept niet gevonden") }

            else -> {
                val r = recipe!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Inline header i.p.v. tweede app bar
                    DetailHeader(
                        title = if (r.isLocal) "Recept bewerken" else "Recept",
                        onBackClick = onBackClick
                    )

                    // Afbeelding in kaart
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            val model = when {
                                imageUri != null -> imageUri
                                r.imageUrl != null -> r.imageUrl
                                else -> null
                            }
                            if (model != null) {
                                AsyncImage(
                                    model = model,
                                    contentDescription = r.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) { Text("Geen afbeelding") }
                            }

                            if (r.isLocal) {
                                FilledTonalIconButton(
                                    onClick = {
                                        if (hasPermission) {
                                            val intent = Intent(
                                                Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                            )
                                            galleryLauncher.launch(intent)
                                        } else {
                                            permissionLauncher.launch(permissionHandler.getRequiredPermission())
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Afbeelding wijzigen"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (r.isLocal) {
                        // Naam
                        OutlinedTextField(
                            value = nameText,
                            onValueChange = { nameText = it },
                            label = { Text("Naam") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        // Beschrijving
                        OutlinedTextField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            label = { Text("Beschrijving") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 140.dp),
                            minLines = 6
                        )

                        Spacer(Modifier.height(20.dp))

                        // Eén duidelijke CTA
                        Button(
                            onClick = {
                                focus.clearFocus()
                                viewModel.saveRecipe(nameText, descriptionText, imageUri)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Wijzigingen opslaan")
                        }
                    } else {
                        // Read-only layout
                        Text("Naam", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(r.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        Text("Beschrijving", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(r.description, style = MaterialTheme.typography.bodyLarge)

                        r.instructions?.let {
                            Spacer(Modifier.height(12.dp))
                            Text("Volledige instructies", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

/** Kleine inline header met back-knop — vervangt de tweede TopAppBar. */
@Composable
private fun DetailHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Terug"
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
