package com.example.myapplication_as

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity(), android.hardware.SensorEventListener {

    private var contadorPasoz = 0
    private lateinit var sensorManager: SensorManager
    private var sensorPasos: Sensor? = null
    private lateinit var tvContador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvContador = findViewById(R.id.tvContador)
        val btnSumar = findViewById<Button>(R.id.btnSumar)
        val btnReinisiar = findViewById<Button>(R.id.btnReinisiar)
        val btnDetalle = findViewById<Button>(R.id.btnDetalle)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                100
            )
        } else {
            configurrSensor()
        }

        btnSumar.setOnClickListener {
            contadorPasoz++
            tvContador.text = "Tus pasos de hoy: $contadorPasoz"
        }

        btnReinisiar.setOnClickListener {
            contadorPasoz = 0
            tvContador.text = "Tus pasos de hoy: $contadorPasoz"
        }

        btnDetalle.setOnClickListener {
            val intent = Intent(this, Detail::class.java)
            intent.putExtra("PASOS", contadorPasoz)
            startActivity(intent)
        }

        val cardPasos = findViewById<MaterialCardView>(R.id.cardPasos)

        cardPasos.setOnClickListener {
            cardPasos.isChecked = !cardPasos.isChecked
        }
    }

    private fun configurrSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorPasos = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensorPasos == null) {
            tvContador.text = "No se pudo activar el sensor de pasos"
        }
    }

    override fun onResume() {
        super.onResume()
        sensorPasos?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val pasosTotales = event.values[0].toInt()
            tvContador.text = "Pasos de hoy: $pasosTotales"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configurrSensor()
            } else {
                tvContador.text = "Falta el permiso para contar pasos"
            }
        }
    }
}
