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
    private var minsValue = 1

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

        // displayedValues is reliable on all API levels; setFormatter often leaves a bare "1".
        val hourLabels = Array(24) { h ->
            if (h == 1) getString(R.string.one_hour_label)
            else getString(R.string.n_hours_label, h)
        }
        val minuteLabels = Array(60) { m ->
            if (m == 1) getString(R.string.one_minute_label)
            else getString(R.string.n_minutes_label, m)
        }

        setTimePicker(npHours, max = 23, isMinutes = false, displayedValues = hourLabels)
        setTimePicker(npMinutes, max = 59, isMinutes = true, displayedValues = minuteLabels)

        npHours.value = 0
        npMinutes.value = 1
        hourValue = 0
        minsValue = 1

        binding.saveButton.setOnClickListener {
            val currentActivity = (activity as MainActivity?)!!
            val alertCall = AlertBuilder(currentActivity)

            npHours.clearFocus()
            npMinutes.clearFocus()
            binding.saveButton.requestFocus()

            // NumberPicker often commits getValue() only after focus moves; read on the next
            // frame so the value matches what the wheel shows (avoids saving 10 min when UI shows 1).
            binding.root.post {
                if (!isAdded) return@post
                hourValue = npHours.value
                minsValue = npMinutes.value

                if (hourValue == 0 && minsValue == 0) {
                    alertCall.showInvalidTimeAlert(
                        getString(R.string.time_error),
                        getString(R.string.invalid_time)
                    )
                    return@post
                }

                val session = Session(hourValue, minsValue)
                val sessionTime = session.durationInSeconds

                if (sessionTime <= 0) {
                    return@post
                }
                if (MZPrefs.sessionExists(session.id)) {
                    alertCall.showDupAlert(
                        getString(R.string.duplicate),
                        getString(R.string.already_session)
                    )
                    return@post
                }
                if (!MZPrefs.addSession(session.id, sessionTime)) {
                    return@post
                }
                goBackToFirst()
            }
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
        isMinutes: Boolean,
        displayedValues: Array<String>
    ) {
        numpick.minValue = 0
        numpick.maxValue = max
        numpick.wrapSelectorWheel = true
        numpick.displayedValues = displayedValues

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
