package com.tahir.flickrimagesearcher

import android.app.Application
import com.tahir.flickrimagesearcher.di.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.ksp.generated.module
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpTimber()
        startKoin()
    }

    override fun onTerminate() {
        super.onTerminate()
        GlobalContext.stopKoin()

    }

    private fun startKoin() {
        // Start Koin with the application.
        org.koin.core.context.startKoin {
            androidContext(this@App)
            modules(AppModules().module)
        }
    }

    private fun setUpTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}