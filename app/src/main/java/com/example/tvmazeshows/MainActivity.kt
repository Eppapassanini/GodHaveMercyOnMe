
package com.example.tvmazeshows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint

import com.example.tvmazeshows.ui.detail.ShowDetailScreen
import com.example.tvmazeshows.ui.detail.ShowDetailViewModel
import com.example.tvmazeshows.ui.list.ShowsListScreen
import com.example.tvmazeshows.ui.list.ShowsListViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TvMazeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "shows"
    ) {
        composable("shows") {
            val viewModel: ShowsListViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            ShowsListScreen(
                viewModel = viewModel,
                onShowClick = { showId ->
                    navController.navigate("detail/$showId")
                }
            )
        }
        composable(
            route = "detail/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.IntType })
        ) {
            val viewModel: ShowDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            ShowDetailScreen(
                viewModel = viewModel,
                onNavigateUp = { navController.navigateUp() },
                onShowClick = { showId ->
                    navController.navigate("detail/$showId") {
                        popUpTo("detail/{showId}") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun TvMazeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}