package io.branch.branchsters.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.branch.branchsters.views.HomeScreen
import io.branch.branchsters.views.SplashScreen
import io.branch.branchsters.views.OnboardingScreen
import io.branch.branchsters.views.CreateBranchLinkScreen
import io.branch.branchsters.views.LogsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object CreateLink : Screen("create_link/{questId}") {
        fun createRoute(questId: Int) = "create_link/$questId"
    }
    object Logs : Screen("logs")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onNavigateToGemini = {},
                onNavigateToCreateLink = { questId ->
                    navController.navigate(Screen.CreateLink.createRoute(questId))
                },
                onNavigateToLogs = {
                    navController.navigate(Screen.Logs.route)
                }
            )
        }
        
        composable(
            route = Screen.CreateLink.route,
            arguments = listOf(navArgument("questId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId") ?: 1
            CreateBranchLinkScreen(
                questId = questId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Screen.Logs.route) {
            LogsScreen(branchData = "")
        }
    }
}


