package com.marhabweb.minizendo

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast



class MainActivity : AppCompatActivity() {


    // This will show the list of created sessions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { _ ->
            val addIntent = Intent(this, AddSessionActivity::class.java)
            startActivity(addIntent)
        }

        title = getString(R.string.app_name)

    }

    // On resume, refresh the list, and enable/disable the + button
    override fun onResume() {
        super.onResume()

        val textView:TextView = findViewById(R.id.noListText)
        val listView = findViewById<ListView>(R.id.sessionListView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        val realm = Realm.getDefaultInstance()

        val query = realm.where(Session::class.java)
        val results = query.findAll()

        if (results.count() == 0) {

          setListMode(listView, textView, false)

        } else {
            // Show help text if the list is empty
            setListMode(listView, textView, true)
            val adapter = CustomAdapter(this, results)
            listView.adapter = adapter

            // Limit to 6 sessions
            if (results.count() >= 6)
            {
                fab.setEnabled(false)
                fab.alpha = 0.1F

            } else {
                fab.setEnabled(true)
                fab.alpha = 1.0F
            }
        }

    }

    // Replace the list with text, if the list is empty.
    private fun setListMode(list: View, text: View, active: Boolean) {
        if (active == true) {
            list.visibility = View.VISIBLE
            text.visibility = View.INVISIBLE
        } else {
            list.visibility = View.INVISIBLE
            text.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(getApplicationContext(), "16. onDestroy()", Toast.LENGTH_SHORT).show()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Toast.makeText(getApplicationContext(), "17. onDetachedFromWindow()", Toast.LENGTH_SHORT).show()
    }

    // Ask the user whether they really want to remove this session from the list
    fun showDeleteAlertYesOrNo(session: Session?) {
        alert(getString(R.string.wanna_delete), getString(R.string.delete)) {
            positiveButton(getString(R.string.yes)) {
                val realm = Realm.getDefaultInstance()
                val result = realm.where(Session::class.java).equalTo("id", session?.getId()).findFirst()
                realm.beginTransaction()
                if (result != null) {
                    result.deleteFromRealm()
                    realm.commitTransaction()
                    Resume()

                }
                try {
                    realm.where(Session::class.java).equalTo("id", session?.getId()).findFirst()
                } catch(e: IllegalStateException) {
                    toast(getString(R.string.session_deleted))
                }
            }

            negativeButton(getString(R.string.no)) {
                toast(getString(R.string.no_change))
            }
        }.show()
    }

    // Reload the Activity, to refresh the list...will call onResume
    private fun Resume() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }
}


