package com.marhabweb.minizendo_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.marhabweb.minizendo_update.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private var hourValue = 0
    private var minsValue = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setSessionBack.setOnClickListener {
            goBackToFirst()
        }

        val npHours = binding.npHours
        val npMinutes = binding.npMinutes

        setTimePicker(npHours, max = 23, isMinutes = false)
        setTimePicker(npMinutes, max = 59, isMinutes = true)
        npHours.value = 0
        npMinutes.value = 10
        hourValue = 0
        minsValue = 10

        npHours.setFormatter { i ->
            if (i == 1) getString(R.string.one_hour_label) else getString(R.string.n_hours_label, i)
        }
        npMinutes.setFormatter { i ->
            if (i == 1) getString(R.string.one_minute_label) else getString(R.string.n_minutes_label, i)
        }

        binding.saveButton.setOnClickListener {
            var currentActivity = (activity as MainActivity?)!!
            var alertCall = AlertBuilder(currentActivity)

            hourValue = npHours.value
            minsValue = npMinutes.value

            if (hourValue == 0 && minsValue == 0) {
                alertCall.showInvalidTimeAlert(
                    getString(R.string.time_error),
                    getString(R.string.invalid_time)
                )
                return@setOnClickListener
            }

            val session = Session(hourValue, minsValue)
            val sessionTime = session.durationInSeconds

            if (sessionTime <= 0) {
                return@setOnClickListener
            }
            val allPrefs = MZPrefs.getAllPrefs()
            if (allPrefs == null) {
                return@setOnClickListener
            }
            if (allPrefs.containsKey(session.id)) {
                currentActivity = (activity as MainActivity?)!!
                alertCall = AlertBuilder(currentActivity)
                alertCall.showDupAlert(
                    getString(R.string.duplicate),
                    getString(R.string.already_session)
                )
                return@setOnClickListener
            }
            MZPrefs.addSession(session.id, sessionTime)
            goBackToFirst()
        }
    }

    /** Pops this screen so [FirstFragment] is shown (help when there are no sessions). */
    private fun goBackToFirst() {
        val nav = findNavController()
        if (!nav.popBackStack(R.id.FirstFragment, false)) {
            nav.navigateUp()
        }
    }

    private fun setTimePicker(
        numpick: TimePicker,
        max: Int,
        isMinutes: Boolean
    ) {
        numpick.minValue = 0
        numpick.maxValue = max
        numpick.wrapSelectorWheel = true

        numpick.setOnValueChangedListener { _, _, newVal ->
            if (isMinutes) {
                minsValue = newVal
            } else {
                hourValue = newVal
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
