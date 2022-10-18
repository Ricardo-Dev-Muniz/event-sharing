package com.app.co.event_sharing.di

import android.app.Application
import com.app.co.event_sharing.di.AppModule
import com.app.co.event_sharing.di.KoinUtilities
import org.koin.core.context.loadKoinModules

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        KoinUtilities.loadKoin(applicationContext)
        loadKoinModules(AppModule.eachModules())
    }
}