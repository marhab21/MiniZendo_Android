package com.marhabweb.mini_zendo

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
import io.realm.RealmResults

/**
 * Created by martine on 12/24/17.
 * This is for being able to delete items in the list...
 */
class CustomAdapter(private var activity: Activity, private var items: RealmResults<Session>) : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtName: TextView? = null
        var zenImage: ImageView? = null
        var deleteBtn: Button? = null



        init {
            this.txtName = row?.findViewById<TextView>(R.id.sessionTime)
            this.zenImage = row?.findViewById<ImageView>(R.id.zenImage)
            this.deleteBtn = row?.findViewById<Button>(R.id.deleteButton)

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

        val session = items[position]
        viewHolder.txtName?.text = session?.getTimeDisplay()
        viewHolder.zenImage?.setImageResource(R.drawable.zen)

        // User can click on the symbol and start the session
        viewHolder.zenImage?.setOnClickListener {
            goToSession(
                    activity,
                    session!!
            )
        }

        // User can click on the text, and start the session
        viewHolder.txtName?.setOnClickListener {

            goToSession(
                    activity,
                    session!!
            )
        }

        // User will delete the button
        viewHolder.deleteBtn?.setOnClickListener {

            deleteSession(
                    activity,
                    session
            )
        }

        return view as View
    }

    // Deletion with anko alert
    private fun deleteSession(activity: Activity, session: Session?) {

        val act = activity as MainActivity
        act.showDeleteAlertYesOrNo(session)
    }

    // Move to session activity
    private fun goToSession(activity: Activity, session: Session) {

        val finishIntent = Intent(activity.applicationContext, SessionActivity::class.java)
        finishIntent.putExtra("sessionItem", session.getId())
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.applicationContext.startActivity(finishIntent)
    }

    // Utility functions
    override fun getItem(i: Int): Session? {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}





