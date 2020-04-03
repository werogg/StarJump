package es.jotero.starjump

import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.jotero.starjump.events.StarJumpEvent
import es.jotero.starjump.listeners.StarJumpListener
import java.util.*
import kotlin.system.exitProcess

class StartActivity : AppCompatActivity(), StarJumpListener {

    private var jumps = 0
    private var starJumpEvent : StarJumpEvent? = null
    private var timing : Long = 0
    private var goal : Int? = null
    private var timer = Timer()
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        jumps = 0
        registerListeners()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        goal = intent.getIntExtra("goal", 0)
        progressBar.max = goal!!
        progressBar.progress = 0
        initTimer()
    }

    private fun registerListeners() {
        setupSensorListener()
        setupListeners()
        starJumpEvent?.registerListener(this)
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.surrenderButton).setOnClickListener {
            finished = true
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun setupSensorListener() {
        starJumpEvent = StarJumpEvent(this)
    }

    override fun onStarJump() {
        if (finished) return
        incrementJumps()
        updateViews()
        checkEnd()
        vibrate()
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (jumps != goal)
                vibrator.vibrate(VibrationEffect.createOneShot(250, 100))
            else
                vibrator.vibrate(VibrationEffect.createOneShot(2500, 255))
        }
    }

    private fun checkEnd() {
        if (goal != null && jumps == goal) {
            onGoalReached()
        }
    }

    private fun onGoalReached() {
        finished = true
        val intent = Intent(this, EndActivity::class.java)
        intent.putExtra("goal", goal)
        intent.putExtra("time", timing)
        startActivity(intent)
    }

    private fun incrementJumps() {
        jumps += 1
    }

    private fun updateViews() {
        updateProgressBar()
        updateJumpsText()
    }

    private fun updateProgressBar() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = jumps
    }

    private fun updateJumpsText() {
        val textJumps = findViewById<TextView>(R.id.textJumps)
        textJumps.text = jumps.toString()
    }

    private fun initTimer() {
        timer.schedule(object: TimerTask() {
            override fun run() {
                timing += 1
                runOnUiThread {
                    updateTimeText()
                }
            }
        },0, 1)
    }

    private fun updateTimeText() {
        val timingText = findViewById<TextView>(R.id.textTiming)
        val milliseconds = timing % 1000
        val seconds = (timing / 1000) % 60
        val minutes = (timing / 1000*60) % 60
        //val hours = (timing / 1000*60*60) % 24
        val millisecondsString = when {
            milliseconds < 10 -> "00$milliseconds"
            milliseconds in 10..99 -> "0$milliseconds"
            else -> "$milliseconds"
        }


        timingText.text = "$minutes:$seconds:$millisecondsString"
    }

    var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finished = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.confirm_back_surrender), Toast.LENGTH_SHORT).show();

        Handler().postDelayed({ doubleBackToExitPressedOnce=false; }, 2000);
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        jumps = 0
    }
}