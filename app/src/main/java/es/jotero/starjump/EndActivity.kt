package es.jotero.starjump

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import es.jotero.starjump.enums.SharePlatform

class EndActivity : AppCompatActivity() {

    var goal = 0
    var time : Long = 0
    var milliseconds : Long = 0
    var seconds : Long = 0
    var minutes : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        goal = intent.getIntExtra("goal", 0)
        time = intent.getLongExtra("time", 0)

        updateTexts()
        setupListeners()
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.homeButton).setOnClickListener {
            onHomeButtonClicked()
        }

        findViewById<ImageButton>(R.id.facebookButton).setOnClickListener {
            onFacebookButtonClicked()
        }

        findViewById<ImageButton>(R.id.instagramButton).setOnClickListener {
            onInstagramButtonClicked()
        }

        findViewById<ImageButton>(R.id.whatsappButton).setOnClickListener {
            onWhatsappButtonClicked()
        }

        findViewById<ImageButton>(R.id.twitterButton).setOnClickListener {
            onTwitterButtonClicked()
        }
    }

    private fun onTwitterButtonClicked() {
        share(SharePlatform.TWITTER)
    }

    private fun onWhatsappButtonClicked() {
        share(SharePlatform.WHATSAPP)
    }

    private fun onInstagramButtonClicked() {
        share(SharePlatform.INSTAGRAM)
    }

    private fun onFacebookButtonClicked() {
        share(SharePlatform.FACEBOOK)
    }

    private fun onGoalButtonClicked() {
        findViewById<SlidingUpPanelLayout>(R.id.sliding_layout).panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun onHomeButtonClicked() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun updateTexts() {
        val jumpsText = findViewById<TextView>(R.id.textJumps)
        val timingText = findViewById<TextView>(R.id.textTiming)
        val descEndText = findViewById<TextView>(R.id.textDescription2)
        milliseconds = time % 1000
        seconds = (time / 1000) % 60
        minutes = (time / 1000*60) % 60
        //val hours = (timing / 1000*60*60) % 24
        var millisecondsString = ""

        millisecondsString = when {
            milliseconds < 10 -> "00$milliseconds"
            milliseconds in 10..99 -> "0$milliseconds"
            else -> "$milliseconds"
        }

        descEndText.text = getString(R.string.congratulations_desc, goal, minutes, seconds, millisecondsString)
        timingText.text = "$minutes:$seconds:$millisecondsString"
        jumpsText.text = goal.toString()


    }

    private fun share(platform: SharePlatform) {
        val message = getString(R.string.share_results_message, goal, minutes, seconds, milliseconds)
        val intent = Intent(Intent.ACTION_SEND)
        val pm = packageManager
        var packageName = ""
        var error = false

        intent.type = "text/plain"

        packageName = when (platform) {
            SharePlatform.FACEBOOK -> "com.facebook.katana"
            SharePlatform.WHATSAPP -> "com.whatsapp"
            SharePlatform.INSTAGRAM -> "com.instagram.android"
            SharePlatform.TWITTER -> "com.twitter.android"
        }

        try {
            val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            intent.setPackage(packageName)
        } catch (exc : PackageManager.NameNotFoundException) {
            Toast.makeText(this, getString(R.string.error_app_not_installed), Toast.LENGTH_SHORT)
                .show()
            error = true
        }

        intent.putExtra(Intent.EXTRA_TEXT, message)

        if (!error) startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
    }
}