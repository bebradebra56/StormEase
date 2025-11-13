package com.stromeese.appsofr.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddActivity : Screen("add_activity")
    object Statistics : Screen("statistics")
    object Achievements : Screen("achievements")
    object Settings : Screen("settings")
    object Archive : Screen("archive")
}

