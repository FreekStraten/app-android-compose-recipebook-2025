package nl.avans.freekstraten.receptenapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nl.avans.freekstraten.receptenapp.navigation.Routes
import nl.avans.freekstraten.receptenapp.ui.theme.Recipebook_MBDA_FreekStratenTheme
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeViewModel

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
    val navController = rememberNavController()
    val recipeViewModel: RecipeViewModel = viewModel()

    // Observe current back stack entry to determine which tab is selected
    val currentRoute = navController.currentDestination?.route
    val selectedTab = when {
        currentRoute?.startsWith(Routes.ONLINE_RECIPES) == true -> 1
        else -> 0
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Receptenboek") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        if (currentRoute != Routes.MY_RECIPES) {
                            navController.navigate(Routes.MY_RECIPES) {
                                // Clear back stack to avoid building up a large stack
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Book, contentDescription = "Mijn Recepten") },
                    label = { Text("Mijn Recepten") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        if (currentRoute != Routes.ONLINE_RECIPES) {
                            navController.navigate(Routes.ONLINE_RECIPES) {
                                // Clear back stack to avoid building up a large stack
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Public, contentDescription = "Online Recepten") },
                    label = { Text("Online Recepten") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            RecipeNavHost(navController, recipeViewModel)
        }
    }
}

@Composable
fun RecipeNavHost(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MY_RECIPES
    ) {
        composable(Routes.MY_RECIPES) {
            MyRecipesScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetailRoute(recipeId))
                }
            )
        }

        composable(Routes.ONLINE_RECIPES) {
            OnlineRecipesScreen(recipeViewModel)
        }

        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}