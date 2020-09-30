package com.example.compass.libs.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*
import kotlin.collections.ArrayList

class SensorController(context: Context) : SensorEventListener {
    /*
     * Sensor Manager and Sensors
     */
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Sensor accuracy
    var sensorAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH

    /*
        Sensor Availability
     */
    var hasGravitySensor = false
    var hasRotationVector = false
    var hasMagFieldSensor = false

    /*
     * Azimuth and direction calculations
     */
    private var mMagReady = false
    private var mGravityReady = false

    // Data from Accelerometer/Gravity and Magnetic Field Sensors
    private var accelVals = FloatArray(3)
    private var magVals = FloatArray(3)

    // Identity matrix
    private val identityMatrix = FloatArray(9)

    // Rotation Matrices
    private val mRotationMatrix = FloatArray(9)

    // Array that contains azimuth, pitch and roll
    private val mOrientation = FloatArray(3)

    /*
     *** Output Values ***
     */
    // Output values received from MF or Rotation Vector Sensor
    var azimuth = 0f
    var pitch = 0f
    var roll = 0f

    // Rolling average value for azimuth filtering
    private val rollingAverageAzimuth: ArrayList<Float>

    /*
        Sensor Values
     */
    var magneticFieldValue = 0f
    var temperatureValue = -274f
    var lightValue = -1
    var pressureValue = -1f
    var humidityValue = -1f
    private val ambientSensorEvent: AmbientSensorEvent
    private var ambientSensorListener: OnAmbientSensorEventListener? = null

    init {

        // Sensor Manager

        // Check Sensor Availability
        hasRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null
        hasGravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null
        hasMagFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null

        // TODO For Testing purpose set flags
        // hasRotationVector = false
        // hasGravitySensor = false
        // hasMagFieldSensor = false
        rollingAverageAzimuth = ArrayList()
        ambientSensorEvent = AmbientSensorEvent()
    }

    /**
     * Register sensor listeners
     *
     * @param sensorDelay sets how much delay should be, SensorManager.SENSOR_DELAY_UI
     */
    fun onResume(sensorDelay: Int) {
        if (hasRotationVector) {
            sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                sensorDelay
            )
        } else {
            if (hasGravitySensor) {
                sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                    sensorDelay
                )
            } else {
                sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    sensorDelay
                )
            }
        }
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            sensorDelay
        )


        // Other Sensors
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), sensorDelay
        )
        sensorManager.registerListener(
            this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
            sensorDelay
        )
        sensorManager.registerListener(
            this, sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY),
            sensorDelay
        )
        sensorManager.registerListener(
            this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
            sensorDelay
        )
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values)
                sensorAccuracy = event.accuracy
            }
            Sensor.TYPE_GRAVITY -> if (!hasRotationVector) {
                accelVals = event.values.clone()
                mGravityReady = true
            }
            Sensor.TYPE_ACCELEROMETER -> if (!hasRotationVector && !hasGravitySensor) {
                accelVals = event.values.clone()
                mGravityReady = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                if (!hasRotationVector) {
                    sensorAccuracy = event.accuracy
                    magVals = event.values.clone()
                    mMagReady = true
                }
                magVals = event.values.clone()
                run {
                    magneticFieldValue = Math
                        .sqrt(
                            magVals[0] * magVals[0] + magVals[1] * magVals[1] + (magVals[2] * magVals[2]).toDouble()
                        ).toFloat()
                    ambientSensorEvent.magneticFieldValue = magneticFieldValue
                }
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                run {
                    temperatureValue = event.values[0]
                    ambientSensorEvent.temperatureValue = temperatureValue
                }
                println("TYPE_AMBIENT_TEMPERATURE temperatureValue $temperatureValue")
            }
            Sensor.TYPE_RELATIVE_HUMIDITY -> {
                run {
                    humidityValue = event.values[0]
                    ambientSensorEvent.humidityValue = humidityValue
                }
                println("TYPE_RELATIVE_HUMIDITY humidityValue $humidityValue")
            }
            Sensor.TYPE_PRESSURE -> {
                run {
                    pressureValue = event.values[0]
                    ambientSensorEvent.pressureValue = pressureValue
                }
                println("TYPE_PRESSURE pressureValue $pressureValue")
            }
            Sensor.TYPE_LIGHT -> lightValue = event.values[0].toInt()
        }

        // Get angles from RV or (Accelerometer/Gravity)sensors if MF sensor available
        if (hasMagFieldSensor) {
            if (hasRotationVector) {
                orientationAnglesFromRVSensor()
            } else {
                orientationAnglesFromMFSensor()
            }
        }
        if (ambientSensorListener != null) {
            ambientSensorListener!!.onSensorAmbientChanges(ambientSensorEvent)
        }
    }// Low-Pass Filter Angles in degree
    // Rolling Average Filter
    /**
     * Get azimuth, pitch and roll using Magnetic Field Sensor and Gravity or
     * Accelerometer Sensors. If available Gravity sensor is the first choice
     */
    private fun orientationAnglesFromMFSensor() {

        val success =
            SensorManager.getRotationMatrix(mRotationMatrix, identityMatrix, accelVals, magVals)
        if (mGravityReady && mMagReady && success) {
            mGravityReady = false
            mMagReady = false
            SensorManager.getOrientation(mRotationMatrix, mOrientation)

            // Low-Pass Filter Angles in degree
            azimuth = SensorFilters.lowPass(
                azimuth,
                (mOrientation[0] * RAD_TO_DEG + 360) % 360,
                ALPHA
            )
            pitch = SensorFilters.lowPass(pitch, -mOrientation[1] * RAD_TO_DEG, ALPHA)
            roll = SensorFilters.lowPass(roll, 90 + mOrientation[2] * RAD_TO_DEG, ALPHA)
            // Rolling Average Filter
            azimuth = SensorFilters.movingAverage(rollingAverageAzimuth, azimuth)
        }
    }

    /**
     * Uses Rotation Vector to retrieve compass values
     */
    private fun orientationAnglesFromRVSensor() {
        SensorManager.getOrientation(mRotationMatrix, mOrientation)
        azimuth = (mOrientation[0] * RAD_TO_DEG + 360) % 360
        pitch = -mOrientation[1] * RAD_TO_DEG
        roll = 90 + mOrientation[2] * RAD_TO_DEG
    }

    interface OnAmbientSensorEventListener {
        fun onSensorAmbientChanges(ambientSensorEvent: AmbientSensorEvent?)
    }

    fun setAmbientSensorListener(ambientSensorListener: OnAmbientSensorEventListener?) {
        this.ambientSensorListener = ambientSensorListener
    }

    companion object {
        // Convert from radian to degrees
        private const val RAD_TO_DEG = (180 / Math.PI).toFloat()
        private const val ALPHA = .15f
    }


}