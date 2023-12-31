package vn.app.common

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
//            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(diModules)
        }
    }
}