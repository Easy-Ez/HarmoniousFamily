package cc.wecando.harmoniousfamily

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            e.printStackTrace()
        }
    }
}