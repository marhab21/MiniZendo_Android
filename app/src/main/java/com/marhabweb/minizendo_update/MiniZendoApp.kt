package com.marhabweb.minizendo_update

import android.app.Application

class MiniZendoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MZPrefs.setup(this)
    }
}
