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
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show recipe image from either URL or URI
            val imageModel = when {
                recipe.imageUri != null -> recipe.imageUri
                recipe.imageUrl != null -> recipe.imageUrl
                else -> null
            }

            // Image section (40% of width)
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Display a placeholder if no image is available
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Geen\nafbeelding",
                            style = AppTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Text section (60% of width)
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Recipe name
                Text(
                    text = recipe.name,
                    style = AppTypography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Recipe description
                Text(
                    text = recipe.description,
                    style = AppTypography.bodyMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Add delete button for local recipes
            if (recipe.isLocal && onDeleteClick != null) {
                IconButton(
                    onClick = onDeleteClick,
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
    }
}