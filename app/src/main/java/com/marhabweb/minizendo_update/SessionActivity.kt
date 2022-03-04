package com.marhabweb.minizendo_update

import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
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

        title = getString(R.string.session)

        // Keep the app open, to make sure the app doesn't go to sleep
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val narrows = findViewById<View>(R.id.narrowsImage)
        val image = narrows.background
        image.alpha = 255

        val sessionToPlay = intent.getStringExtra("sessionItem")

        val sessionItemId = sessionToPlay.toString()
        // Retrieve the session we want
        val duration: Int = MZPrefs.getDurationForId(sessionItemId)!!
        // Create it
        val sessionItem = Session(sessionItemId, duration)
        // Grab progress bar (circle) and number
        progressBar = findViewById(R.id.progressBar)
        textViewCount = findViewById(R.id.textViewCount)

        val time = duration.toLong()
        val milliTime = time * 1000
        timeInMillis = milliTime

        start(sessionItem)

    }

    // Start with a bell
    private fun start(session: Session) {

        progressBar.max = timeInMillis.toInt() / 1000
        isRunning = true
        // Play the bell at start of session
        mPlayer = MediaPlayer.create(applicationContext, R.raw.realbell)
        mPlayer.start()

        countDownTimer = object : CountDownTimer(timeInMillis, 1) {
            // Countdown text is 1 second off from the progress bar
            override fun onTick(millisUntilFinished: Long) {
                progressBar.progress = (millisUntilFinished * 0.001f).roundToInt()
                progressBar.rotation = 360f
                updateTimer(millisUntilFinished / 1000)
            }

            // End with the same bell
            override fun onFinish() {
                textViewCount.text = getString(R.string.zero_time)
                isRunning = false
                progressBar.progress = timeInMillis.toInt() / 1000
                progressBar.max = timeInMillis.toInt() / 1000
                val quote = findViewById<TextView>(R.id.buddhistQuote)
                quote.text = session.addMessage()
                mPlayer = MediaPlayer.create(applicationContext, R.raw.realbell)
                mPlayer.start()
                stop()
                progressBar.visibility = View.INVISIBLE
                textViewCount.visibility = View.INVISIBLE

            }
        }.start()
        countDownTimer.start()
    }

    // Stop the original timer, and start another one to show the quote (10 seconds)
    private fun stop()
    {
        isRunning = false
        countDownTimer.cancel()

        shortTimer = object : CountDownTimer(30000, 1) {

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

    // Seconds and progress are out of phase by 1 second
    fun updateTimer(secondsLeft: Long) {
        val minutes = secondsLeft.toInt() / 60
        val seconds = secondsLeft - minutes * 60 // Seconds left over after division

        var secondsString = seconds.toString()

        if (seconds <= 9) {
            secondsString = "0$secondsString"
        }

        textViewCount.text = getString(R.string.timing_result, minutes.toString(), secondsString)
    }
}