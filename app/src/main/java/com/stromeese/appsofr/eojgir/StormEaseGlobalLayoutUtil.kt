package com.stromeese.appsofr.eojgir

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication

class StormEaseGlobalLayoutUtil {

    private var stormEaseMChildOfContent: View? = null
    private var stormEaseUsableHeightPrevious = 0

    fun stormEaseAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        stormEaseMChildOfContent = content.getChildAt(0)

        stormEaseMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val stormEaseUsableHeightNow = stormEaseComputeUsableHeight()
        if (stormEaseUsableHeightNow != stormEaseUsableHeightPrevious) {
            val stormEaseUsableHeightSansKeyboard = stormEaseMChildOfContent?.rootView?.height ?: 0
            val stormEaseHeightDifference = stormEaseUsableHeightSansKeyboard - stormEaseUsableHeightNow

            if (stormEaseHeightDifference > (stormEaseUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(StormEaseApplication.stormEaseInputMode)
            } else {
                activity.window.setSoftInputMode(StormEaseApplication.stormEaseInputMode)
            }
//            mChildOfContent?.requestLayout()
            stormEaseUsableHeightPrevious = stormEaseUsableHeightNow
        }
    }

    private fun stormEaseComputeUsableHeight(): Int {
        val r = Rect()
        stormEaseMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}