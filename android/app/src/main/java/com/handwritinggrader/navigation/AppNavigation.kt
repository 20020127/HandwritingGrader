package com.handwritinggrader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.handwritinggrader.ui.screens.home.HomeScreen
import com.handwritinggrader.ui.screens.camera.CameraScreen
import com.handwritinggrader.ui.screens.result.ResultScreen
import com.handwritinggrader.ui.screens.wrongquestions.WrongQuestionsScreen
import com.handwritinggrader.ui.screens.statistics.StatisticsScreen
import com.handwritinggrader.ui.screens.history.HistoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Result : Screen("result/{question}/{questionType}/{subject}") {
        fun createRoute(question: String, questionType: String, subject: String): String {
            return "result/$question/$questionType/$subject"
        }
    }
    object WrongQuestions : Screen("wrong_questions")
    object Statistics : Screen("statistics")
    object History : Screen("history")
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
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }
        
        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigateBack = { navController.popBackStack() },
                onImageCaptured = { question, questionType, subject ->
                    navController.navigate(Screen.Result.createRoute(question, questionType, subject))
                }
            )
        }
        
        composable(Screen.Result.route) { backStackEntry ->
            val question = backStackEntry.arguments?.getString("question") ?: ""
            val questionType = backStackEntry.arguments?.getString("questionType") ?: ""
            val subject = backStackEntry.arguments?.getString("subject") ?: ""
            
            ResultScreen(
                question = question,
                questionType = questionType,
                subject = subject,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.WrongQuestions.route) {
            WrongQuestionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
