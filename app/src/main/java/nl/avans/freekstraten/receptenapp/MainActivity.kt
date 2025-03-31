package nl.avans.freekstraten.receptenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.avans.freekstraten.receptenapp.ui.theme.Recipebook_MBDA_FreekStratenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Recipebook_MBDA_FreekStratenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeApp() {
    val recipes = remember {
        listOf(
            Recipe("Pasta Carbonara", "Italiaans gerecht met pasta, ei, kaas en spek"),
            Recipe("Lasagne", "Gelaagd pastagerecht met gehakt en tomatensaus"),
            Recipe("Pizza Margherita", "Traditionele pizza met tomaat, mozzarella en basilicum"),
            Recipe("Tiramisu", "Italiaans dessert met koffie, mascarpone en cacao")
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Receptenboek") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            items(recipes) { recipe ->
                RecipeItem(recipe)
                Divider()
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = recipe.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = recipe.description,
            fontSize = 14.sp
        )
    }
}

data class Recipe(val name: String, val description: String)