package com.stromeese.appsofr.eojgir.presentation.di

import com.stromeese.appsofr.eojgir.data.repo.StormEaseRepository
import com.stromeese.appsofr.eojgir.data.shar.StormEaseSharedPreference
import com.stromeese.appsofr.eojgir.data.utils.StormEasePushToken
import com.stromeese.appsofr.eojgir.data.utils.StormEaseSystemService
import com.stromeese.appsofr.eojgir.domain.usecases.StormEaseGetAllUseCase
import com.stromeese.appsofr.eojgir.presentation.pushhandler.StormEasePushHandler
import com.stromeese.appsofr.eojgir.presentation.ui.load.StormEaseLoadViewModel
import com.stromeese.appsofr.eojgir.presentation.ui.view.StormEaseViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val stormEaseModule = module {
    factory {
        StormEasePushHandler()
    }
    single {
        StormEaseRepository()
    }
    single {
        StormEaseSharedPreference(get())
    }
    factory {
        StormEasePushToken()
    }
    factory {
        StormEaseSystemService(get())
    }
    factory {
        StormEaseGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        StormEaseViFun(get())
    }
    viewModel {
        StormEaseLoadViewModel(get(), get(), get())
    }
}