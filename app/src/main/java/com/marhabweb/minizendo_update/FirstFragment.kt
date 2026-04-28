package com.marhabweb.minizendo_update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marhabweb.minizendo_update.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var listAdapter: SessionListAdapter? = null
    private var itemTouch: ItemTouchHelper? = null
    private val mMaxSessions = 6

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = requireActivity() as MainActivity
        binding.helpFab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        if (listAdapter == null) {
            listAdapter = SessionListAdapter(act)
            binding.sessionRecycler.layoutManager = LinearLayoutManager(context)
            binding.sessionRecycler.adapter = listAdapter
            itemTouch = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ) = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val p = viewHolder.bindingAdapterPosition
                        if (p == RecyclerView.NO_POSITION) return
                        listAdapter?.notifyItemChanged(p)
                        val s = listAdapter?.sessionAt(p) ?: return
                        val alertCall = AlertBuilder(act)
                        alertCall.showDeleteAlert(
                            getString(R.string.delete),
                            getString(R.string.wanna_delete),
                            s.id
                        ) { act.refreshList() }
                    }
                }
            )
            itemTouch?.attachToRecyclerView(binding.sessionRecycler)
        }
    }

    override fun onResume() {
        super.onResume()
        val act = requireActivity() as MainActivity
        val results = MZPrefs.getAllPrefs()
        val numSessions = results?.size ?: 0

        if (numSessions == 0) {
            binding.welcomeScroll.visibility = View.VISIBLE
            binding.helpFab.visibility = View.VISIBLE
            binding.sessionRecycler.visibility = View.GONE
            act.applySessionListHeader(false)
            act.plusButtonVisible(false)
        } else {
            binding.welcomeScroll.visibility = View.GONE
            binding.helpFab.visibility = View.GONE
            binding.sessionRecycler.visibility = View.VISIBLE
            act.applySessionListHeader(true)
            listAdapter?.setItems(MZPrefs.getPrefsArray())
            act.plusButtonVisible(true)
            act.plusButtonEnabled(numSessions < mMaxSessions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
