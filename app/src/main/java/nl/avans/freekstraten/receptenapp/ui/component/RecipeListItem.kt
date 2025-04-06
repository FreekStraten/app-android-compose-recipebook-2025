package nl.avans.freekstraten.receptenapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography

@Composable
fun RecipeListItem(
    recipe: Recipe,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    // We'll use ElevatedCard which has built-in shadow and elevation
    ElevatedCard(
        onClick = onClick, // Using the Card's built-in onClick instead of a clickable modifier
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Show recipe image from either URL or URI
            val imageModel = when {
                recipe.imageUri != null -> recipe.imageUri
                recipe.imageUrl != null -> recipe.imageUrl
                else -> null
            }

            if (imageModel != null) {
                // Just display the image without any clickable behavior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // Display a placeholder if no image is available
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Geen afbeelding",
                        style = AppTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Recipe name
                Text(
                    text = recipe.name,
                    style = AppTypography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // Delete button only for local recipes
                if (recipe.isLocal && onDeleteClick != null) {
                    IconButton(
                        onClick = { onDeleteClick() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Verwijderen",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Recipe description
            Text(
                text = recipe.description,
                style = AppTypography.bodyMedium
            )
        }
    }
}