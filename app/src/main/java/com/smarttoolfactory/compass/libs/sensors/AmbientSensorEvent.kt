package com.smarttoolfactory.compass.libs.sensors

import android.hardware.SensorEvent

class AmbientSensorEvent {

    var sensorEvent: SensorEvent? = null

    /*
     Sensor Values
  */
    var magneticFieldValue = -1f

    // Below abs zero
    var temperatureValue = -274f
    var lightValue = -1
    var pressureValue = -1f
    var humidityValue = -1f
}