package com.marhabweb.minizendo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import io.realm.Realm
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class AddSessionActivity : AppCompatActivity() {

    var hourValue = 0
    var minsValue = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_session)

        val npHours = findViewById<TimePicker>(R.id.npHours)
        val npMinutes = findViewById<TimePicker>(R.id.npMinutes)

        setTimePicker(npHours, 0, 23, true)
        setTimePicker(npMinutes, 1, 59, true)
        npHours.value = 0
        npMinutes.value = 10

        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            println("Hour Value = " + hourValue)
            println("Minute Value = " + minsValue)

            val session = Session(hourValue, minsValue)

            val realm = Realm.getDefaultInstance()
            val result = realm.where(Session::class.java).equalTo("durationInSeconds", session?.durationInSeconds).findFirst()
            if (result != null) {
                 showSimpleAlert()
                 // Leave without adding anything
            } else {

                realm.beginTransaction()
                realm.copyToRealm(session)
                realm.commitTransaction()
                finish()
            }
            // Get the time
        }


    }

    fun showSimpleAlert() {
        alert("You already have a session in the list with this duration","Duplicate Session") {
            positiveButton("OK") {
                toast("No change...")
            }
        }.show()
    }


    private fun setTimePicker(numpick: TimePicker, min: Int, max: Int, wrap: Boolean)  {

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        numpick.setMinValue(min)
        //Specify the maximum value/number of NumberPicker
        numpick.setMaxValue(max)

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numpick.setWrapSelectorWheel(wrap)

        numpick.setOnValueChangedListener  { picker, oldVal, newVal ->
            if (max > 23) { // We are dealing with minutes
                minsValue = numpick.value
            } else {
                hourValue = numpick.value
            }
        }
    }


}
