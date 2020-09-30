package com.example.compass.example.view

import com.example.compass.libs.sensors.SensorController
import java.util.ArrayList
import com.example.compass.libs.sensors.SensorUtils
import com.example.compass.libs.sensors.SensorFilters

class MeasurementController {

    @JvmField
    var azimuth = 0f

    @JvmField
    var direction: String? = null

    @JvmField
    var magValue = 0f

    @JvmField

    var lightValue = 0f
    private var mSensorController: SensorController? = null
    private val movingAverage = ArrayList<Float>()
    fun setSensorController(sensorController: SensorController?) {
        mSensorController = sensorController
    }

    /*
     * ***** COMPASS METHODS *****
     */
    fun update() {
        if (mSensorController != null) {
            azimuth = mSensorController!!.azimuth
            direction = SensorUtils.getDirection(azimuth)
            magValue = SensorFilters.lowPass(mSensorController!!.magneticFieldValue, magValue, .15f)
            magValue = SensorFilters.movingAverage(movingAverage, magValue, 10)
            lightValue =
                SensorFilters.lowPass(mSensorController!!.lightValue.toFloat(), lightValue, .15f)
        }
    }
}