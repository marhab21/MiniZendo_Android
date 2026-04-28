package com.marhabweb.minizendo_update

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class SessionActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var textViewCount: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var shortTimer: CountDownTimer
    private lateinit var mPlayer: MediaPlayer

    internal var isRunning = false
    internal var timeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sessionItemId = intent.getStringExtra("sessionItem") ?: run {
            finish()
            return
        }
        val duration = MZPrefs.getDurationForId(sessionItemId) ?: run {
            finish()
            return
        }
        val sessionItem = Session(sessionItemId, duration)

        progressBar = findViewById(R.id.progressBar)
        textViewCount = findViewById(R.id.textViewCount)
        findViewById<TextView>(R.id.sessionTitle).text = sessionItem.title

        val time = duration.toLong()
        val milliTime = time * 1000
        timeInMillis = milliTime

        start(sessionItem)
    }

    private fun start(session: Session) {

        progressBar.max = timeInMillis.toInt() / 1000
        isRunning = true
        mPlayer = MediaPlayer.create(applicationContext, R.raw.realbell)
        mPlayer.start()

        countDownTimer = object : CountDownTimer(timeInMillis, 1) {
            override fun onTick(millisUntilFinished: Long) {
                progressBar.progress = (millisUntilFinished * 0.001f).roundToInt()
                updateTimer(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                textViewCount.text = getString(R.string.zero_time)
                isRunning = false
                progressBar.progress = timeInMillis.toInt() / 1000
                progressBar.max = timeInMillis.toInt() / 1000
                val quote = findViewById<TextView>(R.id.buddhistQuote)
                findViewById<View>(R.id.timerGroup).visibility = View.INVISIBLE
                findViewById<TextView>(R.id.sessionTitle).visibility = View.INVISIBLE
                quote.text = session.addMessage()
                quote.visibility = View.VISIBLE
                mPlayer = MediaPlayer.create(applicationContext, R.raw.realbell)
                mPlayer.start()
                onSessionComplete()
            }
        }
        countDownTimer.start()
    }

    private fun onSessionComplete() {
        isRunning = false
        shortTimer = object : CountDownTimer(30000, 1) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                finish()
            }
        }
        shortTimer.start()
    }

    override fun onStop() {
        super.onStop()
        if (::mPlayer.isInitialized) {
            mPlayer.release()
        }
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        if (::shortTimer.isInitialized) {
            shortTimer.cancel()
        }
        finish()
    }

    fun updateTimer(secondsLeft: Long) {
        val totalSecs = secondsLeft.toInt()
        val minutes = totalSecs / 60
        var seconds = totalSecs % 60L

        var secondsString = seconds.toString()
        if (seconds <= 9) {
            secondsString = "0$secondsString"
        }
        var minutesString = minutes.toString()
        if (minutes <= 9) {
            minutesString = "0$minutesString"
        }
        textViewCount.text = getString(R.string.timing_result, minutesString, secondsString)
    }
}
