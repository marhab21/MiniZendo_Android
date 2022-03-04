package com.marhabweb.minizendo_update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.marhabweb.minizendo_update.databinding.FragmentSecondBinding

/**
 * This is the CreateSession fragment
 */
@Suppress("RedundantNullableReturnType")
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var hourValue = 0
    private var minsValue = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = (activity as MainActivity?)!!
        parent.plusButtonVisible(false)

        val npHours = binding.npHours
        val npMinutes = binding.npMinutes

        setTimePicker(npHours, 23)
        setTimePicker(npMinutes, 59)
        npHours.value = 0
        npMinutes.value = 10

        val saveButton = binding.saveButton

        saveButton.setOnClickListener {
            // Might be needed, if something is wrong
            var currentActivity = (activity as MainActivity?)!!
            var alertCall = AlertBuilder(currentActivity)

            if (hourValue == 0 && minsValue == 0) {

                alertCall.showInvalidTimeAlert(
                    getString(R.string.time_error),
                    getString(R.string.invalid_time)
                )
            }
            // Create session from the numbers chosen
            val session = Session(hourValue, minsValue)
            // Get the time in sessions
            val sessionTime = session.durationInSeconds

            val allPrefs = MZPrefs.getAllPrefs()
            if (sessionTime > 0) {
                if ( allPrefs != null) {
                    // Checking for a duplicate
                    if (allPrefs.containsKey(session.id)) {
                        currentActivity = (activity as MainActivity?)!!
                        alertCall = AlertBuilder(currentActivity)
                        alertCall.showDupAlert(
                            getString(R.string.duplicate),
                            getString(R.string.already_session)
                        )
                    }  else {
                        // If new, just add it to the preferences
                            MZPrefs.addSession(session.id, sessionTime)
                            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                        }
                    }
                }
            }
        } // OnClickListener

    // Set up pickers with minimum and maximum times
    private fun setTimePicker(
        numpick: TimePicker,
        max: Int
    )  {

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        numpick.minValue = 0
        //Specify the maximum value/number of NumberPicker
        numpick.maxValue = max

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numpick.wrapSelectorWheel = true

        numpick.setOnValueChangedListener  { _, _, _ ->
            if (max > 23) { // We are dealing with minutes
                minsValue = numpick.value
            } else {
                hourValue = numpick.value
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}