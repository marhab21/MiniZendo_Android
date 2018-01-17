package com.marhabweb.minizendo

import android.app.Application
import io.realm.Realm

/**
 * Created by martine on 12/19/17.
 * This is basically to initialize Realm
 */

class MiniZendo : Application() {

    public override fun onCreate () {
        super.onCreate()
        Realm.init(this)
    }
}