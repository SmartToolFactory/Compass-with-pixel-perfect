package com.smarttoolfactory.compass.libs.sensors

import android.content.Context
import android.hardware.SensorManager
import android.hardware.Sensor
import android.location.Location
import android.hardware.GeomagneticField

object SensorUtils {
    fun hasRotationVector(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null
    }

    fun hasMagnetometer(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
    }

    fun hasGravitySensor(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null
    }

    fun getMagneticDeclination(location: Location): Float {
        val geoField = GeomagneticField(
            java.lang.Double.valueOf(location.latitude).toFloat(),
            java.lang.Double.valueOf(location.longitude).toFloat(),
            java.lang.Double.valueOf(location.altitude).toFloat(), System.currentTimeMillis()
        )
        return geoField.declination
    }

    fun getBearing(azimuth: Int): String {
        val bearing: String
        bearing = if (azimuth >= 0 && azimuth < 90) {
            "N" + azimuth + "E"
        } else if (azimuth >= 90 && azimuth < 180) {
            "S" + (azimuth - 90) + "E"
        } else if (azimuth >= 180 && azimuth < 270) {
            "S" + (azimuth - 180) + "W"
        } else {
            "N" + (azimuth - 270) + "W"
        }
        return bearing
    }

    fun getDirection(azimuth: Float): String {
        var direction = "N"
        direction = if (azimuth >= 0 && azimuth < 22.5) {
            "N"
        } else if (azimuth >= 22.5 && azimuth < 67.5) {
            "NE"
        } else if (azimuth >= 67.5 && azimuth < 112.5) {
            "E"
        } else if (azimuth >= 112.5 && azimuth < 157.5) {
            "SE"
        } else if (azimuth >= 157.5 && azimuth < 202.5) {
            "S"
        } else if (azimuth >= 202.5 && azimuth < 247.5) {
            "SW"
        } else if (azimuth >= 247.5 && azimuth < 292.5) {
            "W"
        } else if (azimuth >= 292.5 && azimuth < 337.5) {
            "NW"
        } else {
            "N"
        }
        return direction
    }
}