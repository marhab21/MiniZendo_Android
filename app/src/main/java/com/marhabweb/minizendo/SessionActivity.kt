package com.marhabweb.minizendo

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import io.realm.Realm

class SessionActivity : AppCompatActivity() {

    lateinit private var progressBar: ProgressBar
    lateinit private var textViewCount: TextView
    lateinit private var countDownTimer: CountDownTimer
    lateinit private var shortTimer: CountDownTimer
    lateinit private var mPlayer: MediaPlayer


    internal var isRunning = false
    internal var timeInMillis: Long = 0

    internal var currentSession = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        title = getString(R.string.session)

        val zenGate = findViewById<View>(R.id.zenImage)
        val image = zenGate.background
        image.alpha = 120

        val sessionItemId = intent.getStringExtra("sessionItem")
        val realm = Realm.getDefaultInstance()
        val sessionItem = realm.where(Session::class.java).equalTo("id", sessionItemId).findFirst()


        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        textViewCount = findViewById<TextView>(R.id.textViewCount)

        
            val duration = sessionItem?.durationInSeconds
            val time = (if (duration != null) duration else throw NullPointerException("Expression 'duration' must not be null")).toLong()
            val milliTime = time * 1000
            timeInMillis = milliTime


      //  if (sessionItem != null) {
            start(sessionItem)
      //  }

    }

    private fun start(session: Session) {


        progressBar.max = timeInMillis.toInt() / 1000
        isRunning = true

        mPlayer = MediaPlayer.create(applicationContext, R.raw.bell)
        mPlayer.start()

        countDownTimer = object : CountDownTimer(timeInMillis, 1) {

            override fun onTick(millisUntilFinished: Long) {
                updateTimer(millisUntilFinished / 1000)
                progressBar.progress = Math.round(millisUntilFinished * 0.001f)
                progressBar.rotation = 360f

            }

            override fun onFinish() {
                textViewCount.text = getString(R.string.zero_time)
                isRunning = false
                progressBar.progress = timeInMillis.toInt() / 1000
                progressBar.max = timeInMillis.toInt() / 1000
                val quote = findViewById<TextView>(R.id.suzukiQuote)
                quote.text = session.addMessage()
                mPlayer = MediaPlayer.create(applicationContext, R.raw.realbell)
                mPlayer.start()
                stop()

            }
        }.start()
        countDownTimer.start()
    }

    private fun stop()
    {
        isRunning = false
        countDownTimer.cancel()

        shortTimer = object : CountDownTimer(10000, 1) {

            override fun onTick(millisUntilFinished: Long) {
                     // do nothing
            }

            override fun onFinish() {
                finish()

            }
        }.start()
        shortTimer.start()

    }

    override fun onStop() {
        super.onStop()
        stop()
        mPlayer.release()
        finish()
    }


    fun updateTimer(secondsLeft: Long) {
        val minutes = secondsLeft.toInt() / 60
        val seconds = secondsLeft - minutes * 60 // Seconds left over after division

        var secondsString = seconds.toString()

        if (seconds <= 9) {
            secondsString = "0" + secondsString
        }

        //textViewCount.setText(minutes.toString() + ":" + secondsString)
        textViewCount.setText(getString(R.string.timing_result, minutes.toString(), secondsString))
    }
}
