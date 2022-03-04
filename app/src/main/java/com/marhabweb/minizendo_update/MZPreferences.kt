package com.marhabweb.minizendo_update

import android.content.Context
import android.content.SharedPreferences

// This object manages persistence of sessions for the user.
object MZPrefs {

    private var sharedPrefs: SharedPreferences? = null

    // Called to initialize the prefs
    fun setup(context: Context) {
        sharedPrefs = context.getSharedPreferences("MiniZendo", Context.MODE_PRIVATE)
    }

    // Get the entire list
    @Suppress("UNCHECKED_CAST")
   fun getAllPrefs() : MutableMap<String, Int>? = sharedPrefs?.all as MutableMap<String, Int>?

    // Turn the mutable map into an array if necessary
    fun getPrefsArray(): Array<Pair<String, Int>> {
        val map = getAllPrefs()
        return map?.toList()!!.toTypedArray()
    }

    // Add to the prefs
    fun addSession(id: String, time: Int) {
       val edit = sharedPrefs?.edit()
        if (edit != null) {
            edit.putInt(id, time)
            edit.apply()
        }
    }

    // Delete session
    fun deleteSession(id: String)  {
        val edit = sharedPrefs?.edit()
        if (edit != null) {
            edit.remove(id)
            edit.apply()
        }
    }

    // Get the duration of a particular session
    fun getDurationForId(id: String): Int? {
        return if (sharedPrefs!!.contains(id)) sharedPrefs!!.getInt(id, 0) else null
    }

}