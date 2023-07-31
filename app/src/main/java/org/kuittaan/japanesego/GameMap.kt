package org.kuittaan.japanesego

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

//todo: collect items

class GameMap {

    @Composable
    fun createMap(activity: Activity) {

        val context = LocalContext.current
        var deviceLatLng by remember {
            mutableStateOf(LatLng(0.0, 0.0))
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                2f
            ) { location ->

            }
        }

        LaunchedEffect(Unit) {
            val hasLocationPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            while(true) {
                if (hasLocationPermission) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        5f
                    ) { location ->
                        deviceLatLng = LatLng(location.latitude, location.longitude)
                        Log.e("device position", "${location.latitude} ${location.longitude}")
                    }
                }
                delay(5000)
            }
        }

        // Create style for map
        val styleJson = context.resources.openRawResource(R.raw.stylejson).bufferedReader().use {
            it.readText()
        }
        val mapProperties = MapProperties(
            mapStyleOptions = MapStyleOptions(styleJson)
        )

        // Start the map from current position
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(deviceLatLng.latitude, deviceLatLng.longitude), 1f)
        }

        var lastKnownLocation by remember {
            mutableStateOf<Location?>(null)
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        val locationResult = fusedLocationClient.lastLocation
        locationResult.addOnCompleteListener(context as MainActivity) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                lastKnownLocation = task.result
                deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 8f)
            } else {
                Log.e("TAG", "Exception: %s", task.exception)
            }
        }

        // Create collectible location and update it everry 5 seconds
        var collectibleCoordinate = remember { mutableStateOf(LatLng(0.0, 0.0)) }
        LaunchedEffect(key1 = true) {
            while(true) {
                val value = generateRandomLocation(deviceLatLng, 10, 2000)
                Log.e("collectible position", "$value")
                collectibleCoordinate.value = LatLng(value.latitude, value.longitude)
                delay(5000)
            }
        }

        GoogleMap (
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {
            // Create marker for device position
            MarkerInfoWindowContent(
                state = MarkerState(
                    position = deviceLatLng
                ),
                icon = bitmapDescriptorFromVector(context, R.drawable.flying_bird_icon)
            ) { marker ->
                Text(marker.title ?: "You", color = Color.Red)
            }

            // Create a marker to collect
            MarkerInfoWindowContent(
                state = MarkerState(
                    position = collectibleCoordinate.value
                )
            ) {
                // Check if the marker is close enough to device position. todo: If it is, enable "picking it up"
                Log.e("marker here", "$it")
                val results = FloatArray(3)
                Location.distanceBetween(
                    deviceLatLng.latitude,
                    deviceLatLng.longitude,
                    it.position.latitude,
                    it.position.longitude,
                    results
                )
            }
        }
    }

    private fun generateRandomLocation(currentLocation: LatLng, min: Int, max: Int): LatLng {

        val currentLong: Double = currentLocation.longitude
        val currentLat: Double = currentLocation.latitude

        // 1 Meter = 0.00900900900901 / 1000
        val meterCord = 0.00900900900901 / 1000

        // Generate random Meters between the maximum and minimum Meters
        val r = Random()
        val randomMeters: Int = r.nextInt(max + min)

        // Generate number for directions
        val randomDir: Int = r.nextInt(6)

        // Convert the distance in meters to coordinates
        val metersCordN = meterCord * randomMeters.toDouble()

        // Return coordinate based on direction and distance
        return if (randomDir == 0) {
            LatLng(currentLat + metersCordN, currentLong + metersCordN)
        } else if (randomDir == 1) {
            LatLng(currentLat - metersCordN, currentLong - metersCordN)
        } else if (randomDir == 2) {
            LatLng(currentLat + metersCordN, currentLong - metersCordN)
        } else if (randomDir == 3) {
            LatLng(currentLat - metersCordN, currentLong + metersCordN)
        } else if (randomDir == 4) {
            LatLng(currentLat, currentLong - metersCordN)
        } else {
            LatLng(currentLat - metersCordN, currentLong)
        }
    }

    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {

        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        // Create a bitmap from the drawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // Draw the drawable onto the bitmap
        val canvas = android.graphics.Canvas(bitmap)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}