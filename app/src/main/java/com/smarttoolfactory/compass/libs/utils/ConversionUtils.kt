package com.smarttoolfactory.compass.libs.utils

import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.roundToInt
import kotlin.math.tan

object ConversionUtils {

    fun convertInchFractions(value: Float, fractions: IntArray) {

        // denominator is fixed
        var denominator = 32
        // integer part, can be signed: 1, 0, -3,...
        val integer = value.toInt()
        // numerator: always unsigned (the sign belongs to the integer part)
        // + 0.5 - rounding, nearest one: 37.9 / 64 -> 38 / 64; 38.01 / 64 -> 38 / 64
        var numerator = ((abs(value) - abs(integer)) * denominator + 0.5).toInt()

        // some fractions, e.g. 24 / 64 can be simplified:
        // both numerator and denominator can be divided by the same number
        // since 64 = 2 ** 6 we can try 2 powers only
        // 24/64 -> 12/32 -> 6/16 -> 3/8
        // In general case (arbitrary denominator) use gcd (Greatest Common Divisor):
        // double factor = gcd(denominator, numerator);
        // denominator /= factor;
        // numerator /= factor;
        while (numerator % 2 == 0 && denominator % 2 == 0) {
            numerator /= 2
            denominator /= 2
        }
        fractions[2] = denominator
        fractions[1] = numerator
        fractions[0] = integer + numerator / denominator
    }

    /**
     * Get rounded float value
     *
     * @param digit  represents decimal places
     * @param number to be rounded
     * @return rounded float number
     */
    fun getDecimalPlaceFloat(digit: Int, number: Float): Float {
        var number = number
        when (digit) {
            0 -> number = number.roundToInt().toFloat()
            1 -> number = (number * 10f).roundToInt() / 10f
            2 -> number = (number * 100f).roundToInt() / 100f
        }
        return number
    }

    fun convertDegreesToPercent(degrees: Float): Float {
        var percent = tan(degrees * Math.PI / 180).toFloat()
        return if (!java.lang.Float.isNaN(percent)) {
            percent *= 100
            percent
        } else {
            1000f
        }
    }

    fun convertPercentToDegrees(percent: Float): Float {
        val degrees = (atan(percent / 100.toDouble()) * 180 / Math.PI).toFloat()
        return if (!java.lang.Float.isNaN(degrees)) {
            degrees
        } else {
            90f
        }
    }
}