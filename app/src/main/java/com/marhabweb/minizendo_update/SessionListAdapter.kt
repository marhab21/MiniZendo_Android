package com.marhabweb.minizendo_update

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionListAdapter(
    private val activity: MainActivity
) : RecyclerView.Adapter<SessionListAdapter.Holder>() {

    private var items: List<Pair<String, Int>> = emptyList()

    fun setItems(pairs: Array<Pair<String, Int>>) {
        items = pairs.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_list_row, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val p = items[position]
        val session = Session(p.first, p.second)
        holder.label.text = session.title
        holder.startArea.setOnClickListener {
            goToSession(session)
        }
    }

    fun sessionAt(adapterPosition: Int): Session? {
        if (adapterPosition < 0 || adapterPosition >= items.size) return null
        val p = items[adapterPosition]
        return Session(p.first, p.second)
    }

    private fun goToSession(session: Session) {
        val i = Intent(activity, SessionActivity::class.java)
        i.putExtra("sessionItem", session.id)
        activity.startActivity(i)
    }

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val startArea: View = v.findViewById(R.id.sessionRow)
        val label: TextView = v.findViewById(R.id.sessionTime)
    }
}
