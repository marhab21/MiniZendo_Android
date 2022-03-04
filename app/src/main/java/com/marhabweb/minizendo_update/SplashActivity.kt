package com.marhabweb.minizendo_update

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val timeToLoad: Long = 3000 //3 seconds


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


         Handler(Looper.getMainLooper()).postDelayed({
             val intent = Intent(applicationContext, MainActivity::class.java)
             startActivity(intent)
             finish()
         }, timeToLoad)

    }

}

