package com.marhabweb.minizendo_update

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
            itemTouch = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ) = false

                    override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                    ) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0f) {
                            val itemView = viewHolder.itemView
                            val ctx = recyclerView.context
                            val bg = ColorDrawable(
                                ContextCompat.getColor(ctx, R.color.swipe_delete_background)
                            )
                            bg.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            bg.draw(c)
                            val icon = ContextCompat.getDrawable(ctx, R.drawable.ic_delete_swipe_24)
                            icon?.let {
                                val margin =
                                    (itemView.height - it.intrinsicHeight).coerceAtLeast(0) / 2
                                val top = itemView.top + margin
                                val bottom = top + it.intrinsicHeight
                                val right = itemView.right - margin
                                val left = right - it.intrinsicWidth
                                it.setBounds(left, top, right, bottom)
                                it.draw(c)
                            }
                        }
                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val p = viewHolder.bindingAdapterPosition
                        if (p == RecyclerView.NO_POSITION) return
                        val s = listAdapter?.sessionAt(p) ?: run {
                            listAdapter?.notifyItemChanged(p)
                            return
                        }
                        AlertBuilder(act).showDeleteAlert(
                            getString(R.string.delete),
                            getString(R.string.wanna_delete),
                            s.id,
                            onDeleted = { act.refreshList() },
                            onCancel = { listAdapter?.notifyItemChanged(p) }
                        )
                    }
                }
            )
        }
        binding.sessionRecycler.layoutManager = LinearLayoutManager(context)
        binding.sessionRecycler.adapter = listAdapter
        itemTouch?.attachToRecyclerView(binding.sessionRecycler)
    }

    override fun onResume() {
        super.onResume()
        val act = requireActivity() as MainActivity
        val pairs = MZPrefs.getPrefsArray()
        listAdapter?.setItems(pairs)
        val numSessions = pairs.size

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
            act.plusButtonVisible(true)
            act.plusButtonEnabled(numSessions < mMaxSessions)
        }
    }

    override fun onDestroyView() {
        itemTouch?.attachToRecyclerView(null)
        super.onDestroyView()
        _binding = null
    }
}
