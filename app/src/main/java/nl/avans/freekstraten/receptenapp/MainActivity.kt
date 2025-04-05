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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nl.avans.freekstraten.receptenapp.data.Recipe
import nl.avans.freekstraten.receptenapp.navigation.Routes
import nl.avans.freekstraten.receptenapp.ui.theme.Recipebook_MBDA_FreekStratenTheme
import nl.avans.freekstraten.receptenapp.util.ServiceLocator
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeDetailViewModel
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

    // Create viewModels with ServiceLocator
    val recipeViewModel: RecipeViewModel = viewModel {
        RecipeViewModel()  // Uses ServiceLocator internally
    }

    val myRecipesViewModel: MyRecipesViewModel = viewModel {
        MyRecipesViewModel()  // Uses ServiceLocator internally
    }

    // Create a factory for the RecipeDetailViewModel
    val recipeDetailViewModelFactory = remember {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
                    return RecipeDetailViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // Observe current back stack entry to determine which tab is selected
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedTab = when {
        currentRoute == Routes.ONLINE_RECIPES -> 1
        currentRoute == Routes.MY_RECIPES -> 0
        currentRoute?.startsWith("recipe_detail") == true -> 0
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
                                    inclusive = false
                                }
                                launchSingleTop = true
                                restoreState = true
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
                                    inclusive = false
                                }
                                launchSingleTop = true
                                restoreState = true
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
            RecipeNavHost(
                navController = navController,
                recipeViewModel = recipeViewModel,
                myRecipesViewModel = myRecipesViewModel,
                recipeDetailViewModelFactory = recipeDetailViewModelFactory
            )
        }
    }
}

@Composable
fun RecipeNavHost(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
    myRecipesViewModel: MyRecipesViewModel,
    recipeDetailViewModelFactory: androidx.lifecycle.ViewModelProvider.Factory
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MY_RECIPES
    ) {
        composable(Routes.MY_RECIPES) {
            MyRecipesScreen(
                viewModel = myRecipesViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetailRoute(recipeId))
                },
                onCreateRecipeClick = {
                    navController.navigate(Routes.CREATE_RECIPE)
                }
            )
        }

        composable(Routes.ONLINE_RECIPES) {
            OnlineRecipesScreen(
                viewModel = recipeViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetailRoute(recipeId))
                }
            )
        }

        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""

            // Create the view model with the factory to ensure it gets the shared repository
            val detailViewModel: RecipeDetailViewModel = viewModel(
                factory = recipeDetailViewModelFactory
            )

            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = detailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CREATE_RECIPE) {
            CreateRecipeScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRecipeCreated = { newRecipeId ->
                    // Navigate to the detail screen of the new recipe
                    navController.navigate(Routes.recipeDetailRoute(newRecipeId)) {
                        // Pop up to the main screen to avoid having create screen in the back stack
                        popUpTo(Routes.MY_RECIPES)
                    }
                },
                viewModel = myRecipesViewModel
            )
        }
    }
}