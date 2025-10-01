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
import nl.avans.freekstraten.receptenapp.navigation.Routes
import nl.avans.freekstraten.receptenapp.ui.theme.AppTheme
import nl.avans.freekstraten.receptenapp.util.ServiceLocator
import nl.avans.freekstraten.receptenapp.viewmodel.MyRecipesViewModel
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeDetailViewModel
import nl.avans.freekstraten.receptenapp.viewmodel.RecipeViewModel
import nl.avans.freekstraten.receptenapp.viewmodel.OnlineRecipeDetailViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import nl.avans.freekstraten.receptenapp.ui.theme.AppTypography
import nl.avans.freekstraten.receptenapp.ui.theme.brandTitle


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PreferencesManager in ServiceLocator
        ServiceLocator.initPreferences(applicationContext)

        setContent {
            AppTheme {
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

// Rest of the file remains the same
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
        currentRoute == Routes.ONLINE_RECIPES || currentRoute?.startsWith("online_recipe_detail") == true -> 1
        currentRoute == Routes.MY_RECIPES || currentRoute?.startsWith("recipe_detail") == true || currentRoute == Routes.CREATE_RECIPE -> 0
        else -> 0
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { MealMateTitle() }
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
                                    //Remove all intermediate screens, Except start screen
                                    inclusive = false
                                }
                                //Prevents duplicates of the same screen
                                //If the screen is already at the top, it will not be recreated
                                launchSingleTop = true
                                //Saves the state of the screen if it has been visited before
                                //Prevents reloading of all data
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
    // NavHost configures the navigation graph for the entire app
    NavHost(
        navController = navController,
        startDestination = Routes.MY_RECIPES
    ) {

        composable(Routes.MY_RECIPES) {
            MyRecipesScreen(
                viewModel = myRecipesViewModel,
                onRecipeClick = { recipeId ->
                    // Clear any previous detail screens from the back stack
                    navController.navigate(Routes.recipeDetailRoute(recipeId)) {
                        // Pop up to MY_RECIPES to avoid stacking detail screens
                        popUpTo(Routes.MY_RECIPES) {
                            // Don't include MY_RECIPES itself
                            inclusive = false
                        }
                    }
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
                    // Clear any previous detail screens from the back stack
                    navController.navigate(Routes.onlineRecipeDetailRoute(recipeId)) {
                        // Pop up to ONLINE_RECIPES to avoid stacking detail screens
                        popUpTo(Routes.ONLINE_RECIPES) {
                            // Don't include ONLINE_RECIPES itself
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""

            // Create a new instance of the view model each time
            val detailViewModel: RecipeDetailViewModel = viewModel(
                factory = recipeDetailViewModelFactory,
                key = "recipeDetail_$recipeId" // Important: Use unique key based on recipe ID
            )

            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = detailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.ONLINE_RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""

            // Create a new instance each time with unique key
            val onlineDetailViewModel: OnlineRecipeDetailViewModel = viewModel(
                key = "onlineRecipeDetail_$recipeId" // Important: Use unique key based on recipe ID
            )

            OnlineRecipeDetailScreen(
                recipeId = recipeId,
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = onlineDetailViewModel
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

@Composable
private fun MealMateTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Book,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "MealMate",
            style = AppTypography.brandTitle
        )
    }
}
