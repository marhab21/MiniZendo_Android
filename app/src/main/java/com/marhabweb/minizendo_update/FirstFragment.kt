package com.marhabweb.minizendo_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.marhabweb.minizendo_update.databinding.FragmentFirstBinding

/**
 * Updated on February 23rd, 2022
 * Using Fragment, binding, and prefs for persistence
 */
@Suppress("RedundantNullableReturnType")
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var customAdapter: CustomAdapter? = null
    private val mMaxSessions = 6

    // Instead of findById.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Gets the list of sessions from the Preferences.
    override fun onResume() {
        super.onResume()

        val textView: TextView = binding.noListText
        val listView: ListView = binding.sessionListView
        val parent = (activity as MainActivity?)!!
        parent.plusButtonVisible(true)
        // Get list of sessions
        val results = MZPrefs.getAllPrefs()

        val numSessions = results?.size
        if (numSessions == 0) {
            // Show help text if the list is empty
            setListMode(listView, textView, false)
        } else {
            // Show list
            setListMode(listView, textView, true)
            val list = results as MutableMap<String, Int>
            customAdapter = CustomAdapter(parent, list)
            listView.adapter = customAdapter
            // There is a limited number of sessions
            if (numSessions != null) {
                if (numSessions >= mMaxSessions) {
                    parent.plusButtonEnabled(false)
                } else {
                    parent.plusButtonEnabled(true)
                }
            }
        }
    }

    // Replace the list with text, if the list is empty.
    private fun setListMode(list: View, text: View, active: Boolean) {
        if (active) {
            list.visibility = View.VISIBLE
            text.visibility = View.INVISIBLE
        } else {
            list.visibility = View.INVISIBLE
            text.visibility = View.VISIBLE
        }
    }

    // Clean up fragment
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}