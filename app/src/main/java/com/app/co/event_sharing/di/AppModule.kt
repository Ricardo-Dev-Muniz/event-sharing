package com.app.co.event_sharing.di

import com.app.co.event_sharing.repository.ServiceProvider
import com.app.co.event_sharing.repository.MainRepositoryUseCase
import com.app.co.event_sharing.repository.RepositoryUseCaseImpl
import com.app.co.event_sharing.MainViewModel
import com.app.co.event_sharing.repository.Services
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    fun eachModules() = arrayListOf(
        module,
        repository,
        service
    )

    private val module = module {
        viewModel { MainViewModel(repository = get()) }
    }

    private val repository = module {
        single<MainRepositoryUseCase> { RepositoryUseCaseImpl(get()) }
    }


    private val service = module {
        single {
            ServiceProvider(
                url = "https://5f5a8f24d44d640016169133.mockapi.io/api/",
                headers = listOf(
                    Pair("Content-Type", "application/json"),
                    Pair("Accept", "application/json")
                )
            ).generate() as Services
        }
    }

}