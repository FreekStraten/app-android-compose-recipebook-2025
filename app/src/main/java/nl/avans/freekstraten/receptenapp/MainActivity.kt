package nl.avans.freekstraten.receptenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun RecipeApp() {
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        TabItem("Mijn Recepten", Icons.Default.Home),
        TabItem("Online Recepten", Icons.Default.Search)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> MyRecipesScreen(Modifier.padding(innerPadding))
            1 -> OnlineRecipesScreen(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun MyRecipesScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Test 1",
        modifier = modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
        fontSize = 24.sp
    )
}

@Composable
fun OnlineRecipesScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Test 2",
        modifier = modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
        fontSize = 24.sp
    )
}

data class TabItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)