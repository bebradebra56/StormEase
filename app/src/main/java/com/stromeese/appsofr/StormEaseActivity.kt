package com.stromeese.appsofr

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.stromeese.appsofr.eojgir.StormEaseGlobalLayoutUtil
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication
import com.stromeese.appsofr.eojgir.presentation.pushhandler.StormEasePushHandler
import com.stromeese.appsofr.eojgir.stormEaseSetupSystemBars
import org.koin.android.ext.android.inject

class StormEaseActivity : AppCompatActivity() {

    private val stormEasePushHandler by inject<StormEasePushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stormEaseSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_storm_ease)

        val stormEaseRootView = findViewById<View>(android.R.id.content)
        StormEaseGlobalLayoutUtil().stormEaseAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(stormEaseRootView) { stormEaseView, stormEaseInsets ->
            val stormEaseSystemBars = stormEaseInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val stormEaseDisplayCutout = stormEaseInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val stormEaseIme = stormEaseInsets.getInsets(WindowInsetsCompat.Type.ime())


            val stormEaseTopPadding = maxOf(stormEaseSystemBars.top, stormEaseDisplayCutout.top)
            val stormEaseLeftPadding = maxOf(stormEaseSystemBars.left, stormEaseDisplayCutout.left)
            val stormEaseRightPadding = maxOf(stormEaseSystemBars.right, stormEaseDisplayCutout.right)
            window.setSoftInputMode(StormEaseApplication.stormEaseInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "ADJUST PUN")
                val stormEaseBottomInset = maxOf(stormEaseSystemBars.bottom, stormEaseDisplayCutout.bottom)

                stormEaseView.setPadding(stormEaseLeftPadding, stormEaseTopPadding, stormEaseRightPadding, 0)

                stormEaseView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = stormEaseBottomInset
                }
            } else {
                Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "ADJUST RESIZE")

                val stormEaseBottomInset = maxOf(stormEaseSystemBars.bottom, stormEaseDisplayCutout.bottom, stormEaseIme.bottom)

                stormEaseView.setPadding(stormEaseLeftPadding, stormEaseTopPadding, stormEaseRightPadding, 0)

                stormEaseView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = stormEaseBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Activity onCreate()")
        stormEasePushHandler.stormEaseHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            stormEaseSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        stormEaseSetupSystemBars()
    }
}