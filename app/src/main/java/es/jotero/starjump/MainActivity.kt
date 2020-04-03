package es.jotero.starjump

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import es.jotero.starjump.enums.SharePlatform

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        setupListeners()
        setupFunctionalities()
    }

    private fun setupFunctionalities() {
        val goalPicker = findViewById<NumberPicker>(R.id.goalPicker)

        goalPicker.maxValue = 200
        goalPicker.minValue = 0
        goalPicker.value = 20
    }

    private fun setupListeners() {

        findViewById<ImageButton>(R.id.startButton).setOnClickListener {
            onStartButtonClicked()
        }

        findViewById<ImageButton>(R.id.goalButton).setOnClickListener {
            onGoalButtonClicked()
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


        findViewById<ConstraintLayout>(R.id.constraint_layout).setOnClickListener {
            val panelSlide = findViewById<SlidingUpPanelLayout>(R.id.sliding_layout)
            if (panelSlide.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
                panelSlide.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
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

    private fun onStartButtonClicked() {
        val panelSlide = findViewById<SlidingUpPanelLayout>(R.id.sliding_layout)
        if (panelSlide.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            panelSlide.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            return
        }
        val goalPicker = findViewById<NumberPicker>(R.id.goalPicker)
        val intent = Intent(this, StartActivity::class.java)
        intent.putExtra("goal", goalPicker.value)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val panelSlide = findViewById<SlidingUpPanelLayout>(R.id.sliding_layout)

        if (panelSlide.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
            panelSlide.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        else
            super.onBackPressed()
    }

    private fun share(platform: SharePlatform) {
        val message = getString(R.string.share_message)
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