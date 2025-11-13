package com.stromeese.appsofr.eojgir.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class StormEaseDataStore : ViewModel(){
    val stormEaseViList: MutableList<StormEaseVi> = mutableListOf()
    var stormEaseIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var stormEaseContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var stormEaseView: StormEaseVi

}