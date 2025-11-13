package com.stromeese.appsofr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stromeese.appsofr.ui.screens.achievements.AchievementsScreen
import com.stromeese.appsofr.ui.screens.addactivity.AddActivityScreen
import com.stromeese.appsofr.ui.screens.archive.ArchiveScreen
import com.stromeese.appsofr.ui.screens.home.HomeScreen
import com.stromeese.appsofr.ui.screens.settings.SettingsScreen
import com.stromeese.appsofr.ui.screens.statistics.StatisticsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddActivity = {
                    navController.navigate(Screen.AddActivity.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToAchievements = {
                    navController.navigate(Screen.Achievements.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToArchive = {
                    navController.navigate(Screen.Archive.route)
                }
            )
        }

        composable(Screen.AddActivity.route) {
            AddActivityScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Archive.route) {
            ArchiveScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

