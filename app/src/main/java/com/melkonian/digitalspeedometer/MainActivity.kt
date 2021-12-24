package com.melkonian.digitalspeedometer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.melkonian.datasource.IOnSpeedChangedListener
import com.melkonian.datasource.ISpeedValues
import com.melkonian.datasource.SpeedValuesService
import com.melkonian.digitalspeedometer.databinding.AcMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: AcMainBinding

    private val viewModel: MainViewModel by viewModels()

    private var iRemoteService: ISpeedValues? = null

    private var speedChangeListener: IOnSpeedChangedListener? = object : IOnSpeedChangedListener.Stub() {
        override fun onSpeedValueChanged(newSpeed: Int) {
            binding.speedometerView.updateSpeed(newSpeed)
        }
    }

    private var serviceConnection: ServiceConnection? = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            iRemoteService = ISpeedValues.Stub.asInterface(service)
            iRemoteService?.registerCallback(speedChangeListener)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            iRemoteService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = AcMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        connectToRemoteService()
        hideSystemBars()
    }

    private fun connectToRemoteService() {
        Intent(this, SpeedValuesService::class.java).also { intent ->
            intent.action = SpeedValuesService::class.java.name
            serviceConnection?.let { bindService(intent, it, Context.BIND_AUTO_CREATE) }
        }
    }

    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }

        actionBar?.hide()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE
            || newConfig.orientation != Configuration.ORIENTATION_PORTRAIT
        ) {
            super.onConfigurationChanged(newConfig)
        }
    }

    override fun onStop() {
        super.onStop()
        iRemoteService = null
        serviceConnection = null
        speedChangeListener = null
    }
}