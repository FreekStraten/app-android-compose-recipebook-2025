package nl.avans.freekstraten.receptenapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography

@Composable
fun RecipeDetailScreen(recipeId: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Recipe ID: $recipeId",
            style = AppTypography.titleLarge
        )
    }
}