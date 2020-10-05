package com.smarttoolfactory.compass

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.smarttoolfactory.compass.compassview.CompassView
import com.smarttoolfactory.compass.libs.sensors.SensorController

class MainActivity : AppCompatActivity() {

    private lateinit var compassView: CompassView
    private lateinit var sensorController: SensorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        sensorController = SensorController(this)

        compassView = findViewById(R.id.compass_view)
        compassView.setSensorController(sensorController)
    }

    override fun onResume() {
        super.onResume()
        sensorController.onResume(SensorManager.SENSOR_DELAY_UI)
        compassView.onResume()
    }

    override fun onPause() {
        super.onPause()
        sensorController.onPause()
        sensorController.setAmbientSensorListener(null)
        compassView.onPause()
    }
}