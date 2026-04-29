package com.marhabweb.minizendo_update

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists sessions with [SharedPreferences] (not SQLite). Android stores them as an XML file in
 * the app’s private storage, typically:
 * `/data/data/<applicationId>/shared_prefs/MiniZendo.xml`
 * (visible on an emulator via Device File Explorer, or with `run-as` on a debuggable build).
 */
object MZPrefs {

    private const val SESSION_PREFIX = "mz_session_"

    private var sharedPrefs: SharedPreferences? = null

    fun setup(context: Context) {
        sharedPrefs = context.getSharedPreferences("MiniZendo", Context.MODE_PRIVATE)
    }

    private fun persistKey(id: String) = SESSION_PREFIX + id

    /**
     * Reads seconds for a session key. Prefer [SharedPreferences.getInt] for keys written with
     * [putInt] — that avoids JVM boxed types from [SharedPreferences.getAll] not matching
     * Kotlin `is Int`, which would drop valid sessions (e.g. 1 minute / 60s) from the list.
     */
    private fun readSessionSeconds(prefs: SharedPreferences, key: String): Int? {
        try {
            val v = prefs.getInt(key, 0)
            if (v > 0) return v
        } catch (_: ClassCastException) {
            // Legacy or hand-edited XML may use another type
        }
        val raw = prefs.all[key] ?: return null
        val sec = when (raw) {
            is Number -> raw.toInt()
            is String -> raw.toIntOrNull() ?: return null
            else -> return null
        }
        return sec.takeIf { it > 0 }
    }

    /**
     * All saved sessions as (id, seconds). Id is the duration in seconds as a string (e.g. "60").
     * Uses namespaced keys [SESSION_PREFIX] plus legacy bare numeric keys for older installs.
     */
    fun getSessionPairs(): List<Pair<String, Int>> {
        val prefs = sharedPrefs ?: return emptyList()
        val seen = mutableSetOf<String>()
        val out = mutableListOf<Pair<String, Int>>()
        // Snapshot avoids rare iterator issues; order is stable for legacy + prefixed keys.
        val keys = prefs.all.keys.toList()

        for (key in keys) {
            if (key.startsWith(SESSION_PREFIX)) {
                val id = key.removePrefix(SESSION_PREFIX)
                val sec = readSessionSeconds(prefs, key) ?: continue
                if (id != sec.toString()) continue
                if (seen.add(id)) out.add(id to sec)
            }
        }

        for (key in keys) {
            if (key.startsWith(SESSION_PREFIX)) continue
            if (!key.all { it.isDigit() }) continue
            val sec = readSessionSeconds(prefs, key) ?: continue
            if (key != sec.toString()) continue
            if (key in seen) continue
            if (prefs.contains(persistKey(key))) continue
            seen.add(key)
            out.add(key to sec)
        }

        return out.sortedBy { it.second }
    }

    /**
     * True if [addSession] has already stored this duration id (e.g. "60" for one minute).
     * Uses the same key as storage ([persistKey]); does not depend on scanning [getSessionPairs]
     * so it cannot disagree with what is on disk.
     */
    fun sessionExists(id: String): Boolean {
        val prefs = sharedPrefs ?: return false
        val pk = persistKey(id)
        if (!prefs.contains(pk)) return false
        val sec = readSessionSeconds(prefs, pk) ?: return false
        return id == sec.toString()
    }

    fun getSessionCount(): Int = getSessionPairs().size

    fun getAllPrefs(): Map<String, Int>? {
        if (sharedPrefs == null) return null
        return getSessionPairs().toMap()
    }

    fun getPrefsArray(): Array<Pair<String, Int>> = getSessionPairs().toTypedArray()

    /**
     * Persists under a private key prefix and removes the legacy bare [id] key so storage stays
     * consistent. commit() so navigation runs only after disk is updated.
     */
    fun addSession(id: String, time: Int): Boolean {
        val edit = sharedPrefs?.edit() ?: return false
        edit.putInt(persistKey(id), time)
        edit.remove(id)
        return edit.commit()
    }

    fun deleteSession(id: String): Boolean {
        val edit = sharedPrefs?.edit() ?: return false
        edit.remove(persistKey(id))
        edit.remove(id)
        return edit.commit()
    }

    fun getDurationForId(id: String): Int? {
        val prefs = sharedPrefs ?: return null
        val pk = persistKey(id)
        if (prefs.contains(pk)) {
            val sec = readSessionSeconds(prefs, pk) ?: return null
            return sec.takeIf { id == sec.toString() }
        }
        if (prefs.contains(id)) {
            val sec = readSessionSeconds(prefs, id) ?: return null
            return sec.takeIf { id == sec.toString() }
        }
        return null
    }
}
