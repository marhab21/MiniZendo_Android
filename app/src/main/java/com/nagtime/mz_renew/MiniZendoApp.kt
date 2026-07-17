package com.nagtime.mz_renew

import android.app.Application

class MiniZendoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MZPrefs.setup(this)
    }
}
