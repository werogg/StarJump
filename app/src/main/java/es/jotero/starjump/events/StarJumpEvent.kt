package es.jotero.starjump.events

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.CountDownTimer
import es.jotero.starjump.listeners.AppListener
import es.jotero.starjump.listeners.StarJumpListener
import kotlin.math.sqrt

class StarJumpEvent(applicationContext: Context) : SensorEventListener {

    private val listeners = mutableListOf<StarJumpListener>()
    private var maxModule = 0f
    private var module = 0f
    private var countDownTimer : CountDownTimer? = null
    private var preventDuplications = false

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type != Sensor.TYPE_LINEAR_ACCELERATION)
            return

        val valueX = event.values[0]
        val valueY = event.values[1]
        val valueZ = event.values[2]


        module = sqrt(valueX*valueX + valueY*valueY + valueZ*valueZ)

        if (maxModule < module) maxModule = module

        checkState(module)
    }

    private var isJumping = false
    private var isGoingUp = false
    private var isGoingDown = false

    private fun checkState(modxyz: Float) {

        val alpha = 0.30f // The lower value, highest sensitivity

        if (maxModule > 38 && !isJumping) {
            isJumping = true
            isGoingUp = true
            return
        }

        val halfMaxModule = maxModule - maxModule * alpha

        if (modxyz < halfMaxModule && isJumping && isGoingUp) {
            isGoingUp = false
            isGoingDown = true
            return
        }

        if (modxyz < halfMaxModule && isJumping && isGoingDown) {
            isJumping = false
            isGoingDown = false
            maxModule = 0f
            if (!preventDuplications) {
                onUserJump()
                preventDuplications = true
                countDownTimer?.start()
            }


            return
        }
    }

    fun registerListener(appListener: AppListener) {
        if (appListener is StarJumpListener)
            listeners.add(appListener)
    }

    private fun onUserJump() {
        for (listener in listeners) {
            listener.onStarJump()
        }
    }

    init {
        val sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        countDownTimer = object : CountDownTimer(300, 100) {
            override fun onFinish() {
                preventDuplications = false
            }

            override fun onTick(millisUntilFinished: Long) {
            }

        }
    }
}