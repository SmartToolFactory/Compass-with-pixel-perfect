package com.smarttoolfactory.compass.libs.sensors

import kotlin.math.atan2

object SensorFilters {

    private const val RAD_TO_DEG = (180 / Math.PI).toFloat()
    private const val MAX_SAMPLE_SIZE = 20
    private const val ALPHA = .1f

    fun movingAverage(values: MutableList<Float>, output: Float): Float {
        if (values.size == MAX_SAMPLE_SIZE) {
            values.removeAt(0)
        }
        values.add(output)
        var total = 0f
        for (item in values) {
            total += item
        }
        return total / values.size
    }

    fun movingAverage(values: MutableList<Float>, output: Float, sampleSize: Int): Float {
        if (values.size == sampleSize) {
            values.removeAt(0)
        }
        values.add(output)
        var total = 0f
        for (item in values) {
            total += item
        }
        return total / values.size
    }

    /**
     * Filters noise from signal with threshold ALPHA value, low-frequency
     * **y[i] := y[i-1] + a * (x[i] - y[i-1])**
     *
     * @param input
     * Values retrieved from sensor
     * @param output
     * Current values and final values
     * @return LP filtered output values
     */
    fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input
        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
        return output
    }

    /**
     * Filters noise from signal with threshold ALPHA value, low-frequency
     * **y[i] := y[i-1] + a * (x[i] - y[i-1])**
     *
     * @param input
     * @param output
     * @param alpha
     * @return
     */
    fun lowPass(input: Float, output: Float, alpha: Float): Float {
        return output + alpha * (input - output)
    }

    fun roll(list: MutableList<Float?>, newMember: Float): List<Float?> {
        if (list.size == MAX_SAMPLE_SIZE) {
            list.removeAt(0)
        }
        list.add(newMember)
        return list
    }

    fun averageList(tallyUp: List<Float>?): Float {

        if (tallyUp == null || tallyUp.isEmpty()) {
            return 0f
        }
        var total = 0f
        for (item in tallyUp) {
            total += item
        }
        total /= tallyUp.size
        return total
    }

    fun convertToAngle(y: Float, x: Float): Float {
        return (atan2(y.toDouble(), x.toDouble()) * RAD_TO_DEG).toFloat()
    }
}