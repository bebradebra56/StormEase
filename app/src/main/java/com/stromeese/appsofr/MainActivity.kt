package com.stromeese.appsofr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.stromeese.appsofr.data.preferences.UserPreferences
import com.stromeese.appsofr.navigation.NavGraph
import com.stromeese.appsofr.ui.theme.StormEaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences = remember { UserPreferences(applicationContext) }
            val isDarkTheme by userPreferences.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())

            StormEaseTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}