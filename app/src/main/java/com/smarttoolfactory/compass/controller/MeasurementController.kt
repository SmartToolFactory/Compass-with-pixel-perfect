package com.smarttoolfactory.compass.controller

import com.smarttoolfactory.compass.libs.sensors.SensorController
import java.util.ArrayList
import com.smarttoolfactory.compass.libs.sensors.SensorUtils
import com.smarttoolfactory.compass.libs.sensors.SensorFilters

class MeasurementController {

    var azimuth = 0f

    var direction: String? = null

    var magValue = 0f

    var lightValue = 0f
    private var sensorController: SensorController? = null
    private val movingAverage = ArrayList<Float>()

    fun setSensorController(sensorController: SensorController?) {
        this.sensorController = sensorController
    }

    /*
     * ***** COMPASS METHODS *****
     */
    fun update() {
        if (sensorController != null) {
            azimuth = sensorController!!.azimuth
            direction = SensorUtils.getDirection(azimuth)
            magValue = SensorFilters.lowPass(sensorController!!.magneticFieldValue, magValue, .15f)
            magValue = SensorFilters.movingAverage(movingAverage, magValue, 10)
            lightValue =
                SensorFilters.lowPass(sensorController!!.lightValue.toFloat(), lightValue, .15f)
        }
    }
}