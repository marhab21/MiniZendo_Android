package com.marhabweb.minizendo_update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView


/**
 * Created by martine on 12/24/17.
 * Updated on 02/23/2022
 * This is for being able to delete items in the list...
 */

class CustomAdapter(private var activity: Activity, private var items: MutableMap<String, Int>) : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtName: TextView? = null
        var zenImage: ImageView? = null
        var deleteBtn: Button? = null

        init {
            this.txtName = row?.findViewById(R.id.sessionTime)
            this.zenImage = row?.findViewById(R.id.zenImage)
            this.deleteBtn = row?.findViewById(R.id.deleteButton)

        }
    }

    // Decorating each item with a little zen symbol and a delete button
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.session_item, parent, false)
            viewHolder = ViewHolder(view)

            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        // Fill in the view holder for display
        val itemsList: Array<Pair<String, Int>> = MZPrefs.getPrefsArray()
        val item = itemsList[position]
        val session = Session(item.first, item.second)
        viewHolder.txtName?.text = session.title
        viewHolder.zenImage?.setImageResource(R.drawable.zen)

        // User can click on the symbol and start the session
        viewHolder.zenImage?.setOnClickListener {
            goToSession(
                activity,
                session
            )
        }

        // User can click on the text, and start the session
        viewHolder.txtName?.setOnClickListener {
            goToSession(
                activity,
                session
            )
        }

        // User will delete the session
        viewHolder.deleteBtn?.setOnClickListener {
            deleteSession(
                activity,
                session
            )
        }
        return view as View
    }

    // Deletion with choice alert
    private fun deleteSession(activity: Activity, session: Session?) {

        val act = activity as MainActivity
        with(act) {
            val alertCall = AlertBuilder(act)
            if (session != null) {
                alertCall.showDeleteAlert(
                    getString(R.string.delete),
                    getString(R.string.wanna_delete),
                    session.id
                ) { act.refreshList() }
            }
        }
    }

    // Move to session activity
    private fun goToSession(activity: Activity, session: Session) {

        val finishIntent = Intent(activity.applicationContext, SessionActivity::class.java)
        finishIntent.putExtra("sessionItem", session.id)
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.applicationContext.startActivity(finishIntent)
    }

    // Utility functions
    override fun getItem(i: Int): Pair<String, Int> {
        val itemsList: Array<Pair<String, Int>> = MZPrefs.getPrefsArray()
        return itemsList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}





