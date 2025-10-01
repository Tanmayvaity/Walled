package com.example.walled

import android.app.Application
import com.example.walled.di.appModule
import org.koin.core.context.startKoin

class WalledApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}