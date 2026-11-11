package com.byted.myfitafy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.byted.myfitafy.ui.BottomNavigationBar
import com.byted.myfitafy.ui.EditorScreen
import com.byted.myfitafy.ui.HomeScreen
import com.byted.myfitafy.ui.theme.MyFitafyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.byted.myfitafy.ui.SplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFitafyTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // On surveille l'écran actuel pour savoir si on affiche la barre du bas
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        // On affiche la BottomBar SEULEMENT si on n'est PAS sur le splash
        bottomBar = {
            if (currentRoute != "splash") {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "splash", // <-- ON CHANGE LE DÉPART ICI
            modifier = Modifier.padding(innerPadding)
        ) {
            // Route Splash
            composable("splash") {
                SplashScreen(navController)
            }

            // Route Accueil
            composable("home") {
                HomeScreen(navController)
            }

            // Route Collection
            composable("collection") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Page Collection à venir")
                }
            }

            // Route Éditeur
            composable(
                route = "editor/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "men"
                EditorScreen(category = category)
            }
        }
    }
}