package io.branch.branchster.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.branch.branchster.views.SplashScreen
import io.branch.branchster.views.OnboardingScreen
import io.branch.branchster.views.DetailsScreen
import io.branch.branchster.views.HomeScreen
import io.branch.branchster.views.LogsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object CreateLink : Screen("create_link/{questId}") {
        fun createRoute(questId: Int, monster: String, level: Int) =
            "create_link/$questId/${monster}/$level"
    }
    object Logs : Screen("logs")
    object Details : Screen("deeplink/{branchData}") {
        fun createRoute(branchData: String) = "deeplink/$branchData"
    }

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

//        composable(route = Screen.Home.route) {
//            HomeScreen(
//                navController = navController,
//                onNavigateToGemini = {},
//                onNavigateToCreateLink = { questId ->
//                    navController.navigate(Screen.CreateLink.createRoute(questId, ))
//                },
//                onNavigateToLogs = {
//                    navController.navigate(Screen.Logs.route)
//                }
//            )
//        }

//        composable(
//            route = "create_link/{questId}/{monster}/{level}",
//            arguments = listOf(
//                navArgument("questId") { type = NavType.IntType },
//                navArgument("monster") { type = NavType.StringType },
//                navArgument("level") { type = NavType.IntType }
//            )
//        ) { backStackEntry ->
//            val questId = backStackEntry.arguments?.getInt("questId") ?: 1
//            val monster = backStackEntry.arguments?.getString("monster") ?: ""
//            val level = backStackEntry.arguments?.getInt("level") ?: 1
//
//            CreateBranchLinkScreen(
//                questId = questId,
//                monster = monster,
//                level = level,
//                onBack = { navController.popBackStack() }
//            )
//        }

//        composable(
//            route = Screen.CreateLink.route,
//            arguments = listOf(navArgument("questId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val questId = backStackEntry.arguments?.getInt("questId") ?: 1
//            CreateBranchLinkScreen(
//                questId = questId,
//                onBack = {
//                    navController.popBackStack()
//                }
//            )
//        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                navController = navController
            )
        }

        composable(
            route = "create_link/{questId}/{monster}/{level}",
            arguments = listOf(
                navArgument("questId") { type = NavType.IntType },
                navArgument("monster") { type = NavType.StringType },
                navArgument("level") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId") ?: 1
            val monster = backStackEntry.arguments?.getString("monster") ?: ""
            val level = backStackEntry.arguments?.getInt("level") ?: 1
        }

        composable(route = Screen.Logs.route) {
            LogsScreen(branchData = "")
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("branchData") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedBranchData = backStackEntry.arguments?.getString("branchData") ?: ""
            val branchData = Uri.decode(encodedBranchData)
            DetailsScreen(branchData = branchData, onDismiss = { navController.popBackStack() })
        }

    }
}