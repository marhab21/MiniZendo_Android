package com.marhabweb.minizendo_update

import android.app.Activity
import android.app.AlertDialog.*
import android.widget.Toast

/* This class handles alerts for the application */

open class AlertBuilder (private val currentUI: Activity) {

       private var dialogBuilder = Builder(currentUI)

       private fun showAlert(title: String) {
           // create dialog box
           val alert = dialogBuilder.create()
           // set title for alert dialog box
           alert.setTitle(title)
           // show alert dialog
           alert.show()
       }

       // User deletes a session
       fun showDeleteAlert(
            title: String,
            message: String,
            sessionId: String,
            callback: () ->Unit

        ) {
            dialogBuilder.setMessage(message)
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton(R.string.ok) { _, _ ->
                    MZPrefs.deleteSession(sessionId)
                    val confirm = R.string.session_deleted
                    toast(confirm, currentUI)
                    callback()
                }
                // negative button text and action
                .setNegativeButton(R.string.no) { _, _ ->
                    val nochange = R.string.no_change
                    toast(nochange, currentUI)
                }

           showAlert(title)
        }

        // Confirm what just happened
        private fun toast(text: Int, activity: Activity) = Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()

        // User is creating a duplicate session
        fun showDupAlert(title: String, message: String) {
            // set message of alert dialog
            dialogBuilder.setMessage(message)
                // if the dialog is cancelable
                .setCancelable(true)
                // positive button text and action
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.cancel()
                    val nochange = R.string.no_change
                    toast(nochange, currentUI)
                }
            showAlert(title)
        }

    // User is has a 0 0 time choice
    fun showInvalidTimeAlert(title: String, message: String) {
        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(true)
            // positive button text and action
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.cancel()
            }
        showAlert(title)
    }
}


