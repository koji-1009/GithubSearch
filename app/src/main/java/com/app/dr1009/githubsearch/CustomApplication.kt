package com.app.dr1009.githubsearch

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }
}
