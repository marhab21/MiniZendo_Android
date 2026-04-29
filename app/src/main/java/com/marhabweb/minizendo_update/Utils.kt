package com.marhabweb.minizendo_update

import android.app.Activity
import android.app.AlertDialog
import android.widget.Toast

/* This class handles alerts for the application */

open class AlertBuilder(private val currentUI: Activity) {

    private fun toast(text: Int) =
        Toast.makeText(currentUI, text, Toast.LENGTH_SHORT).show()

    /**
     * @param onDeleted called after the session is removed from prefs
     * @param onCancel called when the user dismisses without deleting (restore list row after swipe)
     */
    fun showDeleteAlert(
        title: String,
        message: String,
        sessionId: String,
        onDeleted: () -> Unit,
        onCancel: () -> Unit = {}
    ) {
        AlertDialog.Builder(currentUI)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { _, _ ->
                if (MZPrefs.deleteSession(sessionId)) {
                    toast(R.string.session_deleted)
                    onDeleted()
                }
            }
            .setNegativeButton(R.string.no) { _, _ ->
                toast(R.string.no_change)
                onCancel()
            }
            .setOnCancelListener {
                onCancel()
            }
            .show()
    }

    fun showDupAlert(title: String, message: String) {
        AlertDialog.Builder(currentUI)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.cancel()
                toast(R.string.no_change)
            }
            .show()
    }

    fun showInvalidTimeAlert(title: String, message: String) {
        AlertDialog.Builder(currentUI)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}
