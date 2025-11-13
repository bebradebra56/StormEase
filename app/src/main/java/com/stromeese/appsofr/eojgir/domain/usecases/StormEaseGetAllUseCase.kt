package com.stromeese.appsofr.eojgir.domain.usecases

import android.util.Log
import com.stromeese.appsofr.eojgir.data.repo.StormEaseRepository
import com.stromeese.appsofr.eojgir.data.utils.StormEasePushToken
import com.stromeese.appsofr.eojgir.data.utils.StormEaseSystemService
import com.stromeese.appsofr.eojgir.domain.model.StormEaseEntity
import com.stromeese.appsofr.eojgir.domain.model.StormEaseParam
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication

class StormEaseGetAllUseCase(
    private val stormEaseRepository: StormEaseRepository,
    private val stormEaseSystemService: StormEaseSystemService,
    private val stormEasePushToken: StormEasePushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : StormEaseEntity?{
        val params = StormEaseParam(
            stormEaseLocale = stormEaseSystemService.stormEaseGetLocale(),
            stormEasePushToken = stormEasePushToken.stormEaseGetToken(),
            stormEaseAfId = stormEaseSystemService.stormEaseGetAppsflyerId()
        )
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Params for request: $params")
        return stormEaseRepository.stormEaseGetClient(params, conversion)
    }



}