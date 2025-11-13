package com.stromeese.appsofr.eojgir

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat

class StormEaseSystemBarsController(private val activity: Activity) {

    private val stormEaseWindow = activity.window
    private val stormEaseDecorView = stormEaseWindow.decorView

    fun stormEaseSetupSystemBars() {
        val isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stormEaseSetupForApi30Plus(isLandscape)
        } else {
            stormEaseSetupForApi24To29(isLandscape)
        }
    }

    private fun stormEaseSetupForApi30Plus(isLandscape: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(stormEaseWindow, false)

            val insetsController = stormEaseWindow.insetsController ?: return

            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isLandscape) {
                insetsController.hide(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
                )
            } else {
                insetsController.hide(WindowInsets.Type.navigationBars())
                insetsController.show(WindowInsets.Type.statusBars())
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun stormEaseSetupForApi24To29(isLandscape: Boolean) {
        var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        if (isLandscape) {
            flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            stormEaseWindow.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        stormEaseDecorView.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stormEaseWindow.navigationBarColor = android.graphics.Color.TRANSPARENT
            // Светлые иконки навигации (если нужно)
            stormEaseDecorView.systemUiVisibility = flags and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }


}

fun Activity.stormEaseSetupSystemBars() {
    StormEaseSystemBarsController(this).stormEaseSetupSystemBars()
}
