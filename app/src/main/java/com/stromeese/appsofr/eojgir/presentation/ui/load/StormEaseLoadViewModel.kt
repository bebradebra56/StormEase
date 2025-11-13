package com.stromeese.appsofr.eojgir.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stromeese.appsofr.eojgir.data.shar.StormEaseSharedPreference
import com.stromeese.appsofr.eojgir.data.utils.StormEaseSystemService
import com.stromeese.appsofr.eojgir.domain.usecases.StormEaseGetAllUseCase
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseAppsFlyerState
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StormEaseLoadViewModel(
    private val stormEaseGetAllUseCase: StormEaseGetAllUseCase,
    private val stormEaseSharedPreference: StormEaseSharedPreference,
    private val stormEaseSystemService: StormEaseSystemService
) : ViewModel() {

    private val _stormEaseHomeScreenState: MutableStateFlow<StormEaseHomeScreenState> =
        MutableStateFlow(StormEaseHomeScreenState.StormEaseLoading)
    val stormEaseHomeScreenState = _stormEaseHomeScreenState.asStateFlow()

    private var stormEaseGetApps = false


    init {
        viewModelScope.launch {
            when (stormEaseSharedPreference.stormEaseAppState) {
                0 -> {
                    if (stormEaseSystemService.stormEaseIsOnline()) {
                        StormEaseApplication.stormEaseConversionFlow.collect {
                            when(it) {
                                StormEaseAppsFlyerState.StormEaseDefault -> {}
                                StormEaseAppsFlyerState.StormEaseError -> {
                                    stormEaseSharedPreference.stormEaseAppState = 2
                                    _stormEaseHomeScreenState.value =
                                        StormEaseHomeScreenState.StormEaseError
                                    stormEaseGetApps = true
                                }
                                is StormEaseAppsFlyerState.StormEaseSuccess -> {
                                    if (!stormEaseGetApps) {
                                        stormEaseGetData(it.stormEaseData)
                                        stormEaseGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _stormEaseHomeScreenState.value =
                            StormEaseHomeScreenState.StormEaseNotInternet
                    }
                }
                1 -> {
                    if (stormEaseSystemService.stormEaseIsOnline()) {
                        if (StormEaseApplication.STORM_EASE_FB_LI != null) {
                            _stormEaseHomeScreenState.value =
                                StormEaseHomeScreenState.StormEaseSuccess(
                                    StormEaseApplication.STORM_EASE_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > stormEaseSharedPreference.stormEaseExpired) {
                            Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Current time more then expired, repeat request")
                            StormEaseApplication.stormEaseConversionFlow.collect {
                                when(it) {
                                    StormEaseAppsFlyerState.StormEaseDefault -> {}
                                    StormEaseAppsFlyerState.StormEaseError -> {
                                        _stormEaseHomeScreenState.value =
                                            StormEaseHomeScreenState.StormEaseSuccess(
                                                stormEaseSharedPreference.stormEaseSavedUrl
                                            )
                                        stormEaseGetApps = true
                                    }
                                    is StormEaseAppsFlyerState.StormEaseSuccess -> {
                                        if (!stormEaseGetApps) {
                                            stormEaseGetData(it.stormEaseData)
                                            stormEaseGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Current time less then expired, use saved url")
                            _stormEaseHomeScreenState.value =
                                StormEaseHomeScreenState.StormEaseSuccess(
                                    stormEaseSharedPreference.stormEaseSavedUrl
                                )
                        }
                    } else {
                        _stormEaseHomeScreenState.value =
                            StormEaseHomeScreenState.StormEaseNotInternet
                    }
                }
                2 -> {
                    _stormEaseHomeScreenState.value =
                        StormEaseHomeScreenState.StormEaseError
                }
            }
        }
    }


    private suspend fun stormEaseGetData(conversation: MutableMap<String, Any>?) {
        val stormEaseData = stormEaseGetAllUseCase.invoke(conversation)
        if (stormEaseSharedPreference.stormEaseAppState == 0) {
            if (stormEaseData == null) {
                stormEaseSharedPreference.stormEaseAppState = 2
                _stormEaseHomeScreenState.value =
                    StormEaseHomeScreenState.StormEaseError
            } else {
                stormEaseSharedPreference.stormEaseAppState = 1
                stormEaseSharedPreference.apply {
                    stormEaseExpired = stormEaseData.stormEaseExpires
                    stormEaseSavedUrl = stormEaseData.stormEaseUrl
                }
                _stormEaseHomeScreenState.value =
                    StormEaseHomeScreenState.StormEaseSuccess(stormEaseData.stormEaseUrl)
            }
        } else  {
            if (stormEaseData == null) {
                _stormEaseHomeScreenState.value =
                    StormEaseHomeScreenState.StormEaseSuccess(stormEaseSharedPreference.stormEaseSavedUrl)
            } else {
                stormEaseSharedPreference.apply {
                    stormEaseExpired = stormEaseData.stormEaseExpires
                    stormEaseSavedUrl = stormEaseData.stormEaseUrl
                }
                _stormEaseHomeScreenState.value =
                    StormEaseHomeScreenState.StormEaseSuccess(stormEaseData.stormEaseUrl)
            }
        }
    }


    sealed class StormEaseHomeScreenState {
        data object StormEaseLoading : StormEaseHomeScreenState()
        data object StormEaseError : StormEaseHomeScreenState()
        data class StormEaseSuccess(val data: String) : StormEaseHomeScreenState()
        data object StormEaseNotInternet: StormEaseHomeScreenState()
    }
}