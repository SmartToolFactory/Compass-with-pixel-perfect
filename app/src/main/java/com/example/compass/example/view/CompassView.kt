package com.example.compass.example.view

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import com.example.compass.libs.sensors.SensorController

class CompassView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var isPortrait = false

    private val drawerCompass: CompassDrawer by lazy {
        CompassDrawer(
            context,
            measurementController
        )
    }

    // Measurement Controller calculates rotation and display angles depending on
    // different states
    private val measurementController: MeasurementController by lazy { MeasurementController() }

    private val handler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            // Your logic code here.
        }
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            measurementController.update()
            invalidate()
            handler.removeCallbacks(this)
            handler.postDelayed(this, 1000 / 60.toLong())
        }
    }


    init {
        init(context)
        // setLayerType(LAYER_TYPE_SOFTWARE, null);
    }


    fun setSensorController(sensorController: SensorController?) {
        measurementController.setSensorController(sensorController)
    }

    private fun init(context: Context) {
        val displayMetrics = resources.displayMetrics
        isPortrait =
            displayMetrics.heightPixels.toFloat() / displayMetrics.widthPixels.toFloat() > 1.4f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)
        val rawWidth = MeasureSpec.getSize(widthMeasureSpec)
        val rawHeight = MeasureSpec.getSize(heightMeasureSpec)
        var width = ((if (isPortrait) 1.0f else 0.8f) * rawWidth.toFloat()).toInt()
        var height = (rawWidth.toFloat() * 0.86f).toInt()

        // TODO Set Screen Dimensions
        if (modeWidth == MeasureSpec.EXACTLY) {
            width = rawWidth
        } else if (modeWidth == Int.MIN_VALUE) {
            width = width.coerceAtMost(rawWidth)
        }

        // Set Height
        if (modeHeight == MeasureSpec.EXACTLY) {
            height = rawHeight
        } else if (modeHeight == Int.MIN_VALUE) {
            height = height.coerceAtMost(rawHeight)
        }

        // TODO Set Square Dimensions
        if (isPortrait) {
            height = width
        } else {
            width = height
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWith: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWith, oldHeight)
        drawerCompass.onSizeChanged(width, height, oldWith, oldHeight)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.post(runnable)
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(runnable)
        super.onDetachedFromWindow()
        dispose()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawerCompass.draw(canvas)
    }

    fun onPause() = Unit
    fun onResume() = Unit
    private fun dispose() {
        drawerCompass.dispose()
    }


}