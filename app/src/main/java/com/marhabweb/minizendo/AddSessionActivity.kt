package com.marhabweb.minizendo
/**
 * Created by martine on 12/19/17.
 * Setting up the Time Pickers.
 */
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import io.realm.Realm
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class AddSessionActivity : AppCompatActivity() {

    var hourValue = 0
    var minsValue = 10

    // Get everything we need to start timers.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_session)

        val npHours = findViewById<TimePicker>(R.id.npHours)
        val npMinutes = findViewById<TimePicker>(R.id.npMinutes)

        setTimePicker(npHours, 0, 23, true)
        setTimePicker(npMinutes, 1, 59, true)
        npHours.value = 0
        npMinutes.value = 10

        title=getString(R.string.app_name)

        val saveButton = findViewById<Button>(R.id.saveButton)

        // Clicking saves the session into the list.
        saveButton.setOnClickListener {

            val session = Session(hourValue, minsValue)

            val realm = Realm.getDefaultInstance()

            // Eliminate a new session which would be a duplicate time
            val result = realm.where(Session::class.java).equalTo("durationInSeconds", session.durationInSeconds).findFirst()
            if (result != null) {
                 showSimpleAlert()
                 // Leave without adding anything
            } else {
                realm.beginTransaction()
                realm.copyToRealm(session)
                realm.commitTransaction()
                finish()
            }

        }
    }

    // Using Anko alerts, to tell user that this is a duplicate, and nothing will be created.
    fun showSimpleAlert() {
        alert(getString(R.string.already_session), getString(R.string.duplicate)) {
            positiveButton(getString(R.string.ok)) {
                toast(getString(R.string.no_change))
            }
        }.show()
    }

    // Set up pickers with minimum and maximum times
    private fun setTimePicker(numpick: TimePicker, min: Int, max: Int, wrap: Boolean)  {

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        numpick.setMinValue(min)
        //Specify the maximum value/number of NumberPicker
        numpick.setMaxValue(max)

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numpick.setWrapSelectorWheel(wrap)

        numpick.setOnValueChangedListener  { _, _, newVal ->
            if (max > 23) { // We are dealing with minutes
                minsValue = numpick.value
            } else {
                hourValue = numpick.value
            }
        }
    }

}
