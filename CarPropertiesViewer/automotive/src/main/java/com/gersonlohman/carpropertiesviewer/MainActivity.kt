/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gersonlohman.carpropertiesviewer

import android.app.Activity
import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple activity that demonstrates connecting to car API and processing car property change
 * events.
 *
 * <p>Please see https://developer.android.com/reference/android/car/packages for API documentation.
 */
class MainActivity : Activity() {
    companion object {
        private const val TAG = "MainActivity"

        // Values are taken from android.car.hardware.CarSensorEvent class.
        // https://android.googlesource.com/platform/packages/services/Car/+/master/car-lib/src/android/car/hardware/CarSensorEvent.java
        private val VEHICLE_GEARS = mapOf(
            0x0000 to "GEAR_UNKNOWN",
            0x0001 to "GEAR_NEUTRAL",
            0x0002 to "GEAR_REVERSE",
            0x0004 to "GEAR_PARK",
            0x0008 to "GEAR_DRIVE",
            0x0010 to "GEAR_FIRST",
            0x0020 to "GEAR_SECOND",
            0x0040 to "GEAR_THIRD",
            0x0080 to "GEAR_FOURTH",
            0x0100 to "GEAR_FIFTH",
            0x0200 to "GEAR_SIXTH",
            0x0400 to "GEAR_SEVENTH",
            0x0800 to "GEAR_EIGHTH",
            0x1000 to "GEAR_NINTH",
            0x2000 to "GEAR_TENTH"
        )

        private val VEHICLE_IGNITION_STATES = mapOf(
            0 to "IGNITION_STATE_UNDEFINED",
            1 to "IGNITION_STATE_LOCK",
            2 to "IGNITION_STATE_OFF",
            3 to "IGNITION_STATE_ACC",
            4 to "IGNITION_STATE_ON",
            5 to "IGNITION_STATE_START"
        )
    }

    private lateinit var currentGearTextView: TextView
    private lateinit var ignitionStateTextView: TextView
    private lateinit var gearSelectionTextView: TextView
    private lateinit var parkingBreakTextView: TextView

    /** Car API. */
    private lateinit var car: Car

    /**
     * An API to read VHAL (vehicle hardware access layer) properties. List of vehicle properties
     * can be found in {@link VehiclePropertyIds}.
     */
    // https://developer.android.com/reference/android/car/hardware/property/CarPropertyManager
    private lateinit var carPropertyManager: CarPropertyManager

    private var carPropertyCurrentGearListener = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<Any>) {
            Log.d(TAG, "Received on changed car property event")
            // value.value type changes depending on the vehicle property.
            currentGearTextView.text = value.value.toString()
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            Log.w(TAG, "Received error car property event, propId=$propId, zone=$zone")
        }
    }

    private var carPropertyIgnitionStateListener = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<Any>) {
            Log.d(TAG, "Received on changed car property event")
            // value.value type changes depending on the vehicle property.
            ignitionStateTextView.text = VEHICLE_IGNITION_STATES.getOrDefault(value.value as Int, "IGNITION_STATE_INVALID")
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            Log.w(TAG, "Received error car property event, propId=$propId, zone=$zone")
        }
    }

    private var carPropertyGearSelectionListener = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<Any>) {
            Log.d(TAG, "Received on changed car property event")
            // value.value type changes depending on the vehicle property.
            gearSelectionTextView.text = VEHICLE_GEARS.getOrDefault(value.value as Int, "GEAR_INVALID")
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            Log.w(TAG, "Received error car property event, propId=$propId, zone=$zone")
        }
    }

    private var carPropertyParkingBreakListener = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<Any>) {
            Log.d(TAG, "Received on changed car property event")
            // value.value type changes depending on the vehicle property.
            parkingBreakTextView.text = value.value.toString()
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            Log.w(TAG, "Received error car property event, propId=$propId, zone=$zone")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentGearTextView = findViewById(R.id.currentGearTextView)
        ignitionStateTextView = findViewById(R.id.ignitionStateTextView)
        gearSelectionTextView = findViewById(R.id.gearSelectionTextView)
        parkingBreakTextView = findViewById(R.id.parkingBreakTextView)

        // createCar() returns a "Car" object to access car service APIs. It can return null if
        // car service is not yet ready but that is not a common case and can happen on rare cases
        // (for example car service crashes) so the receiver should be ready for a null car object.
        //
        // Other variants of this API allows more control over car service functionality (such as
        // handling car service crashes graciously). Please see the SDK documentation for this.
        car = Car.createCar(this)
        carPropertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager

        // Subscribes to the gear change events.
        carPropertyManager.registerCallback(
                carPropertyCurrentGearListener,
                VehiclePropertyIds.CURRENT_GEAR,
                CarPropertyManager.SENSOR_RATE_ONCHANGE
        )

        // Subscribes to the ignition state events.
        carPropertyManager.registerCallback(
            carPropertyIgnitionStateListener,
            VehiclePropertyIds.IGNITION_STATE,
            CarPropertyManager.SENSOR_RATE_ONCHANGE
        )

        // Subscribes to the gear selection events.
        carPropertyManager.registerCallback(
            carPropertyGearSelectionListener,
            VehiclePropertyIds.GEAR_SELECTION,
            CarPropertyManager.SENSOR_RATE_ONCHANGE
        )

        // Subscribes to the parking break events.
        carPropertyManager.registerCallback(
            carPropertyParkingBreakListener,
            VehiclePropertyIds.PARKING_BRAKE_ON,
            CarPropertyManager.SENSOR_RATE_ONCHANGE
        )

        // https://developer.android.com/reference/android/car/VehiclePropertyIds
        // 289408009    IGNITION_STATE
        // 289408001    CURRENT_GEAR
        // 289408000    GEAR_SELECTION
        // 287310850    PARKING_BRAKE_ON

        // Setup the view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val data = arrayListOf<String>()

        // Create a list of properties in debug window
        val carPropertyList = carPropertyManager.propertyList
        for (i in 0 until carPropertyList.size) {
            val carProperty = carPropertyList[i]
            data.add(carProperty.toString())
            //data.add(carProperty.propertyId.toString())
        }

        // Hook it to the adapter
        val adapter = CustomAdapter(this, data)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()

        car.disconnect()
    }
}

