package com.marhabweb.minizendo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val addIntent = Intent(this, AddSessionActivity::class.java)
            startActivity(addIntent)
        }

        title = "Mini Zendo"

    }

    override fun onResume() {
        super.onResume()

        val realm = Realm.getDefaultInstance()

        val query = realm.where(Session::class.java)
        val results = query.findAll()

        val listView = findViewById<ListView>(R.id.sessionListView)

        val adapter = CustomAdapter(this, results)
        listView.adapter = adapter

    }



    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(getApplicationContext(), "16. onDestroy()", Toast.LENGTH_SHORT).show()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Toast.makeText(getApplicationContext(), "17. onDetachedFromWindow()", Toast.LENGTH_SHORT).show()
    }


    fun showDeleteAlertYesOrNo(session: Session?) {
        alert("Do you want to remove this session from the list?","Delete Session?") {
            positiveButton("YES") {
                val realm = Realm.getDefaultInstance()
                val result = realm.where(Session::class.java).equalTo("id", session?.getId()).findFirst()
                realm.beginTransaction()
                if (result != null) {
                    result.deleteFromRealm()
                    realm.commitTransaction()
                    onResume()
                }
                try {
                    val check = realm.where(Session::class.java).equalTo("id", session?.getId()).findFirst()
                } catch(e: IllegalStateException) {
                    toast("Session Deleted!")
                }
            }

            negativeButton("NO") {
                toast("No action taken")
            }
        }.show()
    }

    //General code here
 /*   private fun showAlertWithThreeButton() {
        val alertDilog = AlertDialog.Builder(this@MainActivity).create()
        alertDilog.setTitle("Alert")
        alertDilog.setMessage("Show Alert with three Button")

        alertDilog.setButton(AlertDialog.BUTTON_POSITIVE, "POSITIVE", {
            dialogInterface, i ->
            Toast.makeText(applicationContext, "You clicked on POSITIVE Button", Toast.LENGTH_SHORT).show()
        })

        alertDilog.setButton(AlertDialog.BUTTON_NEGATIVE, "NEGATIVE", {
            dialogInterface, j ->
            Toast.makeText(applicationContext, "You clicked on NEGATIVE Button", Toast.LENGTH_SHORT).show()
        })
        alertDilog.setButton(AlertDialog.BUTTON_NEUTRAL, "NEUTRAL", {
            dialogInterface, k ->
            Toast.makeText(applicationContext, "You clicked on NEUTRAL Button", Toast.LENGTH_SHORT).show()
        })

        alertDilog.show()
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}


