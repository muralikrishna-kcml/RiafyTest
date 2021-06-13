package com.android.riafytest

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application(), LifecycleObserver, Configuration.Provider {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        val myConfig = Configuration.Builder()
            .build()
        WorkManager.initialize(this, myConfig)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}