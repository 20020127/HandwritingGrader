package com.handwritinggrader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.handwritinggrader.ui.screens.home.HomeScreen
import com.handwritinggrader.ui.screens.camera.CameraScreen
import com.handwritinggrader.ui.screens.result.ResultScreen
import com.handwritinggrader.ui.screens.wrongquestions.WrongQuestionsScreen
import com.handwritinggrader.ui.screens.statistics.StatisticsScreen
import com.handwritinggrader.ui.screens.history.HistoryScreen
import com.handwritinggrader.ui.screens.settings.SettingsScreen
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Result : Screen("result/{question}/{questionType}/{subject}/{filePath}") {
        fun createRoute(question: String, questionType: String, subject: String, filePath: String): String {
            val encodedQ = URLEncoder.encode(question, "UTF-8")
            val encodedT = URLEncoder.encode(questionType, "UTF-8")
            val encodedS = URLEncoder.encode(subject, "UTF-8")
            val encodedF = URLEncoder.encode(filePath, "UTF-8")
            return "result/$encodedQ/$encodedT/$encodedS/$encodedF"
        }
    }
    object WrongQuestions : Screen("wrong_questions")
    object Statistics : Screen("statistics")
    object History : Screen("history")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                onNavigateToWrongQuestions = { navController.navigate(Screen.WrongQuestions.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigateBack = { navController.popBackStack() },
                onImageCaptured = { question, questionType, subject, filePath ->
                    navController.navigate(Screen.Result.createRoute(question, questionType, subject, filePath))
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("question") { type = NavType.StringType },
                navArgument("questionType") { type = NavType.StringType },
                navArgument("subject") { type = NavType.StringType },
                navArgument("filePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val question = URLDecoder.decode(backStackEntry.arguments?.getString("question") ?: "", "UTF-8")
            val questionType = URLDecoder.decode(backStackEntry.arguments?.getString("questionType") ?: "", "UTF-8")
            val subject = URLDecoder.decode(backStackEntry.arguments?.getString("subject") ?: "", "UTF-8")
            val filePath = URLDecoder.decode(backStackEntry.arguments?.getString("filePath") ?: "", "UTF-8")

            ResultScreen(
                question = question,
                questionType = questionType,
                subject = subject,
                filePath = filePath,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.WrongQuestions.route) {
            WrongQuestionsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
