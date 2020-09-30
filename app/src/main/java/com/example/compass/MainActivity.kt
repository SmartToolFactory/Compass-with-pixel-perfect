package com.example.compass

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.compass.example.view.CompassView
import com.example.compass.libs.sensors.SensorController

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