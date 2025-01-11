package com.example.labmobile.sensors

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_PROXIMITY
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProximitySensorMonitor(val context: Context) {
    val isNear: Flow<Boolean> = callbackFlow {
        val sensorManager: SensorManager =
            context.getSystemService(SENSOR_SERVICE) as SensorManager
        val proximitySensor = sensorManager.getDefaultSensor(TYPE_PROXIMITY)
        val proximitySensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                if(event.sensor.type == TYPE_PROXIMITY) {
                    if(event.values[0] == 0f) {
                        channel.trySend(true)
                    } else {
                        channel.trySend(false)
                    }
                }
            }
        }

        sensorManager.registerListener(
            proximitySensorEventListener,
            proximitySensor,
            SENSOR_DELAY_NORMAL
        )

        awaitClose {
            sensorManager.unregisterListener(proximitySensorEventListener)
        }
    }
}