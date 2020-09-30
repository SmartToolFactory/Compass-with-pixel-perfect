package com.example.compass.example.view

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import com.example.compass.R
import com.example.compass.config.UNIT_DEGREE
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class CompassDrawer internal constructor(private val context: Context, private val measurementController: MeasurementController) {
    /*
     * ********* DIMENSIONS *********
     */
    /**
     * Width of the view
     */
    private var widthView = 0

    /**
     * Height of the view
     */
    private var heightView = 0

    /**
     * Radius of the circle view. It's radius of of background and frame combined
     */
    private var radiusCircleView = RADIUS_CIRCLE_VIEW.toFloat()

    /**
     * This is the inner frame radius of the view(Add margin for metallic frame)
     */
    private var radiusBackground = RADIUS_VIEW_BACKGROUND.toFloat()

    /**
     * Radius of bezel circle that has angle lines. Radius is set from outer portion
     * of the lines
     */
    private var radiusBezelLines = RADIUS_BEZEL_OUTER.toFloat()

    /**
     * Radius of bezel for angle text(0 - 360)
     */
    private var radiusBezelNumbers = RADIUS_BEZEL_NUMBERS.toFloat()

    /**
     * Radius of lateral indicators such as magnetic field and accuracy or stringLight
     */
    private var radiusLateralIndicators = RADIUS_LATERAL_INDICATORS.toFloat()

    /**
     * Thickness of metallic frame
     */
    private var frameThickness = FRAME_THICKNESS.toFloat()

    // Dimensions of Rings
    private var radiusRingMiddle = RADIUS_BEZEL_OUTER - 100.toFloat()
    private var radiusRingInner = RADIUS_BEZEL_OUTER - 220.toFloat()

    // Radius for place views and direction text
    protected var mRadiusMiddleCenter = radiusRingInner + (radiusRingMiddle - radiusRingInner) / 2

    // Center position of view
    protected var centerX = 0
    protected var centerY = 0
    private var mPixelScale = 1f

    // Dimensions for Texts
    private var textSizeDirectionCardinal = SIZE_DIRECTION_CARDINAL.toFloat()
    private var textSizeDirectionInterCardinal = SIZE_DIRECTION_INTERCARDINAL.toFloat()
    private var textSizeDirectionAngle = SIZE_DIRECTION_ANGLE.toFloat()
    /*
     * ******* PAINTS *******
     */
    /**
     * Paint for drawing metallic frame
     */
    private val paintFrame = Paint()

    /**
     * Paint for view frame background
     */
    private val paintBackground = Paint()

    /**
     * Paint for shadow of view
     */
    private val paintCompassViewShadow = Paint()

    /**
     * Paint for gray bezel
     */
    private val paintBezelPrimary = Paint()

    /**
     * Paint for white bezel
     */
    private val paintBezelSecondary = Paint()

    /**
     * Paint for bezel text
     */
    private val paintBezelText = Paint()

    /**
     * Paint for center text
     */
    private val paintText = Paint()

    /**
     * Paint for lateral indicators and triangle
     */
    private val pathPaint = Paint()
    /*
     * *** Paths ***
     */
    /**
     * Path for gray bezel
     */
    private var pathPrimary: Path? = null

    /**
     * Path for white bezel
     */
    private var pathSecondary: Path? = null

    /**
     * This path is for drawing arcs on left and right side of the compass
     */
    private val pathSideArcs = Path()

    /**
     * Rectangle that covers text bounds
     */
    private val rectTextBounds = Rect()

    /*
     * **** Colors ****
     */
    private val colorBGOuter = Color.BLACK
    private val colorBGMiddle = Color.rgb(35, 32, 32)
    private val colorBGInner = Color.rgb(125, 125, 125)
    private val colorBezelWhite = Color.WHITE
    private val colorLightGray = Color.LTGRAY
    private val colorLightBlue = Color.rgb(40, 204, 245)
    private val colorDarkGray = Color.rgb(80, 80, 80)
    private val colorText = Color.WHITE
    private var stringLight = "stringLight"

    fun onSizeChanged(width: Int, height: Int, oldWith: Int, oldHeight: Int) {
        heightView = width.coerceAtMost(height)
        widthView = heightView
        mPixelScale = widthView / 1080.0f
        centerX = widthView / 2
        centerY = heightView / 2
        radiusCircleView = realPx(RADIUS_CIRCLE_VIEW.toFloat())
        frameThickness = realPx(FRAME_THICKNESS.toFloat())
        radiusBackground = realPx(RADIUS_VIEW_BACKGROUND.toFloat())
        // Radius of circle that contains bezel numbers
        radiusBezelLines = realPx(RADIUS_BEZEL_OUTER.toFloat())
        radiusBezelNumbers = realPx(RADIUS_BEZEL_NUMBERS.toFloat())

        // Radius of lateral indicator
        radiusLateralIndicators = realPx(RADIUS_LATERAL_INDICATORS.toFloat())
        radiusRingMiddle = realPx(RADIUS_BEZEL_OUTER - 100.toFloat())
        radiusRingInner = realPx(RADIUS_BEZEL_OUTER - 220.toFloat())
        mRadiusMiddleCenter = radiusRingInner + (radiusRingMiddle - radiusRingInner) / 2

        // Size of bezel texts
        textSizeDirectionAngle = realPx(SIZE_DIRECTION_ANGLE.toFloat())
        textSizeDirectionCardinal = realPx(SIZE_DIRECTION_CARDINAL.toFloat())
        textSizeDirectionInterCardinal = realPx(SIZE_DIRECTION_INTERCARDINAL.toFloat())

        // Size of Center texts
        init(context)
    }

    private fun init(context: Context) {
        val positions = floatArrayOf(0.3f, .45f, .55f, .65f)
        val colors = intArrayOf(Color.rgb(130, 130, 130), Color.rgb(180, 180, 180), Color.rgb(190, 190, 190),
                Color.rgb(130, 130, 130))

        //        LinearGradient gradientLinear = new LinearGradient(0, 0, widthView, widthView - 100, colors, positions,
//                TileMode.CLAMP);

//        val gradientLinear = LinearGradient(0, 0, widthView, widthView - 100, colors, positions, TileMode.CLAMP)

        val gradientLinear = LinearGradient(0f, 0f, widthView.toFloat(), (widthView - 100).toFloat(), colors, positions, TileMode.CLAMP)

        paintFrame.shader = gradientLinear
        paintFrame.isAntiAlias = true
        paintFrame.style = Paint.Style.STROKE
        paintFrame.strokeWidth = frameThickness * 2
        paintBackground.isAntiAlias = true
        paintBackground.style = Paint.Style.FILL
        paintBackground.strokeWidth = realPx(3f)

        // TODO For shadow Software Layer for View is required
        // paintCompassViewShadow.setShadowLayer(realPx(6), realPx(6), realPx(6), Color.DKGRAY);

        // Gray Bezel
        paintBezelPrimary.style = Paint.Style.STROKE
        paintBezelPrimary.isAntiAlias = true
        paintBezelPrimary.color = Color.GRAY
        paintBezelPrimary.strokeWidth = realPx(4f)
        paintBezelPrimary.textSize = realPx(64f)

        // White Bezel
        paintBezelSecondary.isAntiAlias = true
        paintBezelSecondary.color = Color.WHITE
        paintBezelSecondary.style = Paint.Style.STROKE
        paintBezelSecondary.textSize = realPx(36f)
        paintBezelSecondary.strokeWidth = realPx(6f)

        // Bezel Text
        paintBezelText.isAntiAlias = true
        paintBezelText.style = Paint.Style.FILL
        paintBezelText.textSize = textSizeDirectionAngle
        paintBezelText.color = Color.WHITE

        // Center Text
        paintText.isAntiAlias = true
        paintText.color = colorText
        paintText.style = Paint.Style.FILL
        paintText.strokeWidth = realPx(20f)
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintText.textSize = realPx(80f)
        paintText.setShadowLayer(realPx(6f), realPx(6f), realPx(6f), Color.DKGRAY)
        pathPaint.isAntiAlias = true
        pathPaint.color = colorDarkGray
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeWidth = realPx(20f)
        stringLight = context.resources.getString(R.string.compass_light)
    }

    fun draw(canvas: Canvas) {

        drawCompassBackground(canvas)

        // Draw Texts
        canvas.save()
        canvas.rotate(-measurementController.azimuth, centerX.toFloat(), centerY.toFloat())

        // Draw Bezel
        drawBezelGray(canvas)
        drawBezelWhite(canvas)
        drawBezelAngleText(canvas)
        drawBezelDirectionText(canvas)
        canvas.restore()

        // Center Text
        drawCenterText(canvas)

        // Frame
        drawFrame(canvas)
        drawMagneticField(canvas)
        drawLightLevel(canvas)
        drawCompassIndicator(canvas)
    }

    private fun drawFrame(canvas: Canvas) {
        // Draw Metallic Frame
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusBackground + frameThickness, paintFrame)

        // Draw Silver Frame Border
        paintBackground.style = Paint.Style.STROKE
        paintBackground.color = Color.rgb(150, 150, 150)
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusCircleView, paintBackground)
    }

    private fun drawCompassBackground(canvas: Canvas) {
        paintBackground.style = Paint.Style.FILL
        // Outer Ring
        paintBackground.color = colorBGOuter
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusCircleView, paintBackground)
        // Middle Ring
        paintBackground.color = colorBGMiddle
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusRingMiddle, paintBackground)
        // Inner Ring
        paintBackground.color = colorBGInner
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusRingInner, paintBackground)

        // Draw Shadow Layer for Compass View
        // canvas.drawCircle(centerX, centerY, mRadiusCircleView, mPaintCompassViewShadow);
    }

    private fun drawBezelGray(canvas: Canvas) {
        if (pathPrimary == null) {
            pathPrimary = Path()
            for (degree in 0..359) {

                // Draw indicator lines
                val cos = Math.cos(degree * Math.PI / 180).toFloat()
                val sin = Math.sin(degree * Math.PI / 180).toFloat()
                var x = radiusBezelLines * cos
                var y = radiusBezelLines * sin
                pathPrimary!!.moveTo(x + centerX, y + centerY)
                var lineWidth = LINE_DEG45.toFloat()
                lineWidth = if (degree % 10 == 0) {
                    LINE_DEG10.toFloat()
                } else if (degree % 5 == 0) {
                    LINE_DEG5.toFloat()
                } else {
                    LINE_DEG1.toFloat()
                }
                x = (radiusBezelLines - realPx(lineWidth)) * cos
                y = (radiusBezelLines - realPx(lineWidth)) * sin
                pathPrimary!!.lineTo(x + centerX, y + centerY)
            }
        }
        canvas.drawPath(pathPrimary!!, paintBezelPrimary)
    }

    private fun drawBezelWhite(canvas: Canvas) {
        if (pathSecondary == null) {
            pathSecondary = Path()
            var degree = 0
            while (degree < 360) {


                // Draw indicator lines
                val cos = Math.cos(degree * Math.PI / 180).toFloat()
                val sin = Math.sin(degree * Math.PI / 180).toFloat()
                var x = radiusBezelLines * cos
                var y = radiusBezelLines * sin
                pathSecondary!!.moveTo(x + centerX, y + centerY)
                val lineWidth = LINE_DEG10.toFloat()
                x = (radiusBezelLines - realPx(lineWidth)) * cos
                y = (radiusBezelLines - realPx(lineWidth)) * sin
                pathSecondary!!.lineTo(x + centerX, y + centerY)
                degree += 10
            }
        }
        canvas.drawPath(pathSecondary!!, paintBezelSecondary)
    }

    private fun drawBezelAngleText(canvas: Canvas) {
        paintBezelText.textSize = textSizeDirectionAngle
        var i = 0
        while (i < 360) {
            val cos = Math.cos(Math.toRadians(i.toDouble())).toFloat()
            val sin = Math.sin(Math.toRadians(i.toDouble())).toFloat()
            val x = cos * radiusBezelNumbers + centerX
            val y = sin * radiusBezelNumbers + centerY

            // Draw outer indicator text
            canvas.save()
            canvas.translate(x, y)
            canvas.rotate(90.0f + i)
            val stringAngle = "" + (i + 90) % 360
            paintBezelText.getTextBounds(stringAngle, 0, stringAngle.length, rectTextBounds)
            canvas.drawText(stringAngle, -rectTextBounds.width() / 2.toFloat(), rectTextBounds.height() / 2.toFloat(), paintBezelText)
            canvas.restore()
            i += 10
        }
    }

    private fun drawBezelDirectionText(canvas: Canvas) {
        var i = 0
        while (i < 360) {
            val cos = Math.cos(Math.toRadians(i.toDouble())).toFloat()
            val sin = Math.sin(Math.toRadians(i.toDouble())).toFloat()
            val x = cos * mRadiusMiddleCenter + centerX
            val y = sin * mRadiusMiddleCenter + centerY

            // Draw outer indicator text
            canvas.save()
            canvas.translate(x, y)
            canvas.rotate(90.0f + i)
            var stringDirection: String? = null
            var cardinalDirection = true
            paintBezelText.color = colorBezelWhite
            when (i) {
                315 -> {
                    cardinalDirection = false
                    stringDirection = "NE"
                }
                0 -> stringDirection = "E"
                45 -> {
                    stringDirection = "SE"
                    cardinalDirection = false
                }
                90 -> stringDirection = "S"
                135 -> {
                    stringDirection = "SW"
                    cardinalDirection = false
                }
                180 -> stringDirection = "W"
                225 -> {
                    stringDirection = "NW"
                    cardinalDirection = false
                }
                else -> {
                    stringDirection = "N"
                    paintBezelText.color = Color.RED
                }
            }
            if (cardinalDirection) {
                paintBezelText.textSize = textSizeDirectionCardinal
            } else {
                paintBezelText.textSize = textSizeDirectionInterCardinal
            }
            paintBezelText.getTextBounds(stringDirection, 0, stringDirection.length, rectTextBounds)
            canvas.drawText(stringDirection, -rectTextBounds.width() / 2.toFloat(), rectTextBounds.height() / 2.toFloat(),
                    paintBezelText)
            canvas.restore()
            i += 45
        }
    }

    private fun drawMagneticField(canvas: Canvas) {
        // This paint also draws rectangle for azimuth indicator
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = colorDarkGray
        val sweepAngle = 80
        val max = 100f
        val magneticField = measurementController.magValue
        var percent = Math.min(1f, magneticField / max)
        percent = percent * sweepAngle

        // Draw magnetic field indicator background
        pathSideArcs.reset()
        val bound = RectF(centerX - radiusLateralIndicators, centerY - radiusLateralIndicators, centerX + radiusLateralIndicators, centerY + radiusLateralIndicators)
        pathSideArcs.addArc(bound, 320f, sweepAngle.toFloat())
        // Debug rect bounds
        //canvas.drawRect(bound, mPathPaint);
        canvas.drawPath(pathSideArcs, pathPaint)


        // Set Arc color depending on magnetic field level
        if (magneticField < 50) {
            pathPaint.color = colorLightBlue
            paintBezelText.color = colorLightBlue
        } else if (magneticField < 65) {
            pathPaint.color = Color.YELLOW
            paintBezelText.color = Color.YELLOW
        } else {
            pathPaint.color = Color.RED
            paintBezelText.color = Color.RED
        }


        // Draw Filled Path for value
        pathSideArcs.reset()
        pathSideArcs.addArc(bound, 320 + sweepAngle - percent, percent)
        canvas.drawPath(pathSideArcs, pathPaint)


        // Draw Texts
        paintBezelText.textSize = textSizeDirectionAngle
        drawText(canvas, 313f, String.format(Locale.US, "%dμT", magneticField.toInt()), radiusLateralIndicators - 5, paintBezelText)
        paintBezelText.color = colorLightGray
        drawText(canvas, 50f, "mag.field", radiusLateralIndicators - 6, paintBezelText)
    }

    private fun drawLightLevel(canvas: Canvas) {
        if (measurementController.lightValue < 0) {
            return
        }

        // This paint also draws rectangle for azimuth indicator
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = colorDarkGray
        val sweepAngle = 80
        val max = 500f
        val lightValue = measurementController.lightValue
        var percent = 1f.coerceAtMost(lightValue / max)
        percent *= sweepAngle

        // Draw magnetic field indicator background
        pathSideArcs.reset()
        val bound = RectF(centerX - radiusLateralIndicators, centerY - radiusLateralIndicators, centerX + radiusLateralIndicators, centerY + radiusLateralIndicators)
        pathSideArcs.addArc(bound, 140f, sweepAngle.toFloat())
        // Debug rect bounds
        //canvas.drawRect(bound, mPathPaint);
        canvas.drawPath(pathSideArcs, pathPaint)


        // Set color
        if (lightValue < 100) {
            pathPaint.color = colorLightGray
            paintBezelText.color = colorLightGray
        } else if (lightValue < 200) {
            pathPaint.color = Color.WHITE
            paintBezelText.color = Color.WHITE
        } else if (lightValue < 500) {
            pathPaint.color = colorLightBlue
            paintBezelText.color = colorLightBlue
        } else if (lightValue < 1000) {
            pathPaint.color = Color.YELLOW
            paintBezelText.color = Color.YELLOW
        } else {
            pathPaint.color = Color.RED
            paintBezelText.color = Color.RED
        }

        // Draw Filled Path for value
        pathSideArcs.reset()
        pathSideArcs.addArc(bound, 140f, percent)
        canvas.drawPath(pathSideArcs, pathPaint)


        // Draw Texts
        paintBezelText.textSize = textSizeDirectionAngle
        drawText(canvas, 227f, String.format(Locale.US, "%dlx", lightValue.toInt()), radiusLateralIndicators - 5, paintBezelText)
        paintBezelText.color = colorLightGray
        drawText(canvas, 132f, stringLight, radiusLateralIndicators - 6, paintBezelText)
    }

    private fun drawText(canvas: Canvas, degree: Float, text: String, radius: Float, paint: Paint) {

        val fm = paint.fontMetrics

        val height = fm.bottom - fm.top + fm.leading
        val cos = cos(Math.toRadians(degree.toDouble())).toFloat()
        val sin = sin(Math.toRadians(degree.toDouble())).toFloat()
        val x = cos * radius + centerX
        val y = sin * radius + centerY

        canvas.save()
        canvas.translate(x, y)

        if (degree > 0 && degree < 180) {
            canvas.rotate(270 + degree)
            canvas.drawText(text, -paint.measureText(text) / 2.0f, height / 2, paint)
        } else {
            canvas.rotate(90 + degree)
            canvas.drawText(text, -paint.measureText(text) / 2.0f, 0f, paint)
        }

        canvas.restore()
    }

    private fun drawCenterText(canvas: Canvas) {
        val azimuth: String = "${measurementController.azimuth.toInt()} $UNIT_DEGREE ${measurementController.direction}"
        paintText.getTextBounds(azimuth, 0, azimuth.length, rectTextBounds)
        canvas.drawText(azimuth, centerX - rectTextBounds.width() / 2.toFloat(), centerY + rectTextBounds.height() / 2.toFloat(),
                paintText)
    }

    private fun drawCompassIndicator(canvas: Canvas) {
        pathPaint.style = Paint.Style.FILL
        pathPaint.color = colorLightBlue
        pathSideArcs.reset()
        val x = centerX.toFloat()
        val length = realPx(30f)
        val y = centerY - radiusBackground
        pathSideArcs.reset()
        pathSideArcs.moveTo(x - length / 2.0f, y - length)
        pathSideArcs.lineTo(x - length / 2.0f, y - length)
        pathSideArcs.lineTo(x + length / 2.0f, y - length)
        pathSideArcs.lineTo(x, y)
        canvas.drawPath(pathSideArcs, pathPaint)
    }

    fun onResume() = Unit

    /**
     * Scale images or drawing according to screen resolution
     *
     * @param width to be scaled
     * @return scaled dimension depending on device resolution
     */
    private fun realPx(width: Float): Float {
        return width * mPixelScale
    }

    fun dispose() {}

    companion object {
        /*
     * ****** DRAWING COMPONENTS ******
     */
        // Width and Height of rectangular view
        private const val WIDTH = 1080
        private const val HEIGHT = 1080

        /**
         * Total radius of circle view
         */
        private const val RADIUS_CIRCLE_VIEW = 480

        /**
         * Frame thickness of circle view
         */
        private const val FRAME_THICKNESS = 24

        /**
         * Radius of circle view minus frame width
         */
        private const val RADIUS_VIEW_BACKGROUND = RADIUS_CIRCLE_VIEW - 2 * FRAME_THICKNESS

        // Radius of bezel lines
        private const val RADIUS_BEZEL_OUTER = RADIUS_VIEW_BACKGROUND - 3

        // Radius of bezel direction numbers
        private const val RADIUS_BEZEL_NUMBERS = RADIUS_BEZEL_OUTER - 60
        private const val RADIUS_BEZEL_DIRECTION_TEXT = RADIUS_BEZEL_OUTER - 140

        /**
         * Radius for lateral indicators such as magnetic field and accuracy or stringLight
         */
        private const val RADIUS_LATERAL_INDICATORS = RADIUS_CIRCLE_VIEW + 24

        // Circle dimensions for different sections
        // OUTER INDICATOR LINE
        private const val LINE_DEG45 = 40
        private const val LINE_DEG10 = 30
        private const val LINE_DEG5 = 24
        private const val LINE_DEG1 = 18

        // DIRECTION TEXT SIZES
        private const val SIZE_DIRECTION_CARDINAL = 60
        private const val SIZE_DIRECTION_INTERCARDINAL = 36
        private const val SIZE_DIRECTION_ANGLE = 28
    }
}