package com.melkonian.digitalspeedometer

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.melkonian.digitalspeedometer.databinding.ViewDigitalSpeedometerBinding
import kotlin.math.floor

class DigitalSpeedometerView : ConstraintLayout {
    companion object {
        private const val DEFAULT_SPEED_TEXT_SIZE = 55f
        private const val DEFAULT_UNIT_TEXT_SIZE = 15f
    }

    constructor(context: Context) : this(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(context)
        initAttributeSet(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
        initAttributeSet(context, attrs)
    }

    private lateinit var binding: ViewDigitalSpeedometerBinding

    private var speedUnitLabel = context.getString(R.string.speed_unt_km_h)
    private var speedTextSize = convertDpToPx(DEFAULT_SPEED_TEXT_SIZE)
    private var unitTextSize = convertDpToPx(DEFAULT_UNIT_TEXT_SIZE)

    private var speedValue = 0
    private var speedTextColor = 0
    private var unitTextColor = 0
    private var bgColor: Int = Color.BLACK

    private var bgDrawable: Drawable? = null

    private var isShowUnit = true
    private var isDisableBgImage = false

    private val viewMainContainer
        get() = binding.viewMainContainer

    private val speedUnitsTextView
        get() = binding.speedUnitsTextView

    private val speedDozensTextView
        get() = binding.speedDozensTextView

    private val speedHundredsTextView
        get() = binding.speedHundredsTextView

    private val speedUnitTextView
        get() = binding.speedUnitTextView

    private fun init(context: Context) {
        binding = ViewDigitalSpeedometerBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)

        speedTextColor = ContextCompat.getColor(context, R.color.speed_value_color)
        unitTextColor = ContextCompat.getColor(context, R.color.unit_text_color)
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return

        val customAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.DigitSpeedView, 0, 0)
        speedTextSize = customAttrs.getDimension(R.styleable.DigitSpeedView_speedTextSize, speedTextSize)
        unitTextSize = customAttrs.getDimension(R.styleable.DigitSpeedView_unitTextSize, unitTextSize)
        speedTextColor = customAttrs.getColor(R.styleable.DigitSpeedView_speedTextColor, speedTextColor)
        unitTextColor = customAttrs.getColor(R.styleable.DigitSpeedView_unitTextColor, unitTextColor)
        speedValue = customAttrs.getInt(R.styleable.DigitSpeedView_speedValue, speedValue)
        bgDrawable = customAttrs.getDrawable(R.styleable.DigitSpeedView_backgroundDrawable)
        bgColor = customAttrs.getColor(R.styleable.DigitSpeedView_bgColor, bgColor)
        isDisableBgImage = customAttrs.getBoolean(R.styleable.DigitSpeedView_disableBackgroundImage, false)
        isShowUnit = customAttrs.getBoolean(R.styleable.DigitSpeedView_showUnit, isShowUnit)

        customAttrs.getInteger(R.styleable.DigitSpeedView_speedUnit, 0).apply {
            speedUnitLabel = when (this) {
                0 -> context.getString(R.string.speed_unt_km_h)
                1 -> context.getString(R.string.speed_unt_mph)
                else -> context.getString(R.string.speed_unt_km_h)
            }
        }

        customAttrs.recycle()

        initAttributeValue()
    }

    private fun initAttributeValue() {
        if (isDisableBgImage) {
            viewMainContainer.setBackgroundResource(0)
            viewMainContainer.setBackgroundColor(bgColor)
        } else {
            bgDrawable?.let { viewMainContainer.background = it }
        }

        binding.hundredsViewBg.textSize = speedTextSize
        binding.dozensViewBg.textSize = speedTextSize
        binding.unitsViewBg.textSize = speedTextSize

        speedUnitsTextView.apply {
            setTextColor(speedTextColor)
            text = (speedValue % 10).toString()
            textSize = speedTextSize
            setShadowLayer(20f, 0f, 0f, speedTextColor)
        }
        speedDozensTextView.apply {
            setTextColor(speedTextColor)
            text = (floor((speedValue.toDouble() / 10) % 10)).toInt().toString()
            textSize = speedTextSize
            setShadowLayer(20f, 0f, 0f, speedTextColor)
        }
        speedHundredsTextView.apply {
            setTextColor(speedTextColor)
            text = (speedValue / 100).toString()
            textSize = speedTextSize
            setShadowLayer(20f, 0f, 0f, speedTextColor)
        }

        speedUnitTextView.visibility = if (isShowUnit) View.VISIBLE else View.GONE
        speedUnitTextView.apply {
            setTextColor(unitTextColor)
            text = speedUnitLabel
            textSize = unitTextSize
            setShadowLayer(20f, 0f, 0f, unitTextColor)
        }
    }

    fun updateSpeed(speed: Int) {
        speedValue = speed

        speedUnitsTextView.text = (speedValue % 10).toString()
        speedDozensTextView.text = (floor((speedValue.toDouble() / 10) % 10)).toInt().toString()
        speedHundredsTextView.text = (speedValue / 100).toString()
    }

    private fun convertDpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}