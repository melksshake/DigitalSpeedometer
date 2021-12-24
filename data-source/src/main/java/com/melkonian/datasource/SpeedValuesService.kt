package com.melkonian.datasource

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.ceil

open class SpeedValuesService : LifecycleService() {
    companion object {
        private const val TASK_DELAY = 400L
        private const val TASK_PERIOD = 350L

        private const val MAX_SPEED_VALUE = 300
        private const val SUM_COEFFICIENT = 1.3
        private const val MULTIPLIER_COEFFICIENT = 7
    }

    private val timer = Timer()
    private lateinit var timerTask: SpeedServiceTimerTask
    private lateinit var binder: ISpeedValuesBinder

    private var i: Int = 1

    override fun onCreate() {
        super.onCreate()
        binder = ISpeedValuesBinder()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    private inner class ISpeedValuesBinder : ISpeedValues.Stub() {
        override fun registerCallback(listener: IOnSpeedChangedListener) {
            lifecycleScope.launch(Dispatchers.Default) {
                timerTask = SpeedServiceTimerTask(listener)
                timer.schedule(timerTask, TASK_DELAY, TASK_PERIOD)
            }
        }
    }

    private inner class SpeedServiceTimerTask(val listener: IOnSpeedChangedListener) : TimerTask() {
        override fun run() {
            listener.onSpeedValueChanged(generateSpeed())
        }
    }

    fun generateSpeed(): Int {
        var sum: Double

        val sin1i: Float = ceil(asin(1 / PI.toFloat()))
        val sin2i: Float = ceil(asin(1 / (2 * PI).toFloat()))
        sum = MULTIPLIER_COEFFICIENT * (i + SUM_COEFFICIENT) * (sin1i + sin2i)

        i++

        if (sum >= MAX_SPEED_VALUE) {
            i = 1
            sum = MAX_SPEED_VALUE.toDouble()
        }

        return sum.toInt()
    }
}