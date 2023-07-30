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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import java.util.Random

//todo: päivitä oma sijainti, luo jutut kartalle, ominaisuus jolla jutut voi kerätä. 頑張れ

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
                        Log.e("position now", "${location.latitude} ${location.longitude}")
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
            position = CameraPosition.fromLatLngZoom(LatLng(deviceLatLng.latitude, deviceLatLng.longitude), 10f)
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

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {
            MarkerInfoWindowContent(
                state = MarkerState(
                    position = deviceLatLng
                ),
                icon = bitmapDescriptorFromVector(context, R.drawable.flying_bird_icon)
            ) { marker ->
                Text(marker.title ?: "You", color = Color.Red)
            }
        }
    }

    private fun getCurrentLocation(context: Context): LatLng {

        var latitude = 1.0
        var longitude = -1.0

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: show error screen if fine location permission is not granted
            Log.d("Error", "No permission to access fine location")
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f
        ) { location ->
            latitude = location.latitude
            longitude = location.longitude
        }

        // returns (1.0,-1.0) if location was not received
        Log.e("data", "$latitude , $longitude")
        return LatLng(latitude, longitude)
    }

    @Composable
    fun createMarkers(amount: Int, activity: Activity) {
        val markerSet = remember { mutableListOf<Unit>() }

        var marker = Marker(
            state =  MarkerState(position = LatLng(10.0, 10.0)),
            title = "collectible",
        )

        markerSet.add(marker)
    }

    @SuppressLint("MissingPermission")
    fun generateCoordinates(minMeters: Int, maxMeters: Int, activity: Activity, onSuccess: (LatLng) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        val meterCord = 0.00900900900901 / 1000
        //Generate random Meters between the maximum and minimum Meters
        val r = Random()
        val randomMeters: Int = r.nextInt(minMeters + maxMeters)
        //then Generating Random numbers for different Methods
        val randomPM: Int = r.nextInt(6)

        //Then we convert the distance in meters to coordinates by Multiplying number of meters with 1 Meter Coordinate
        val metersCordN = meterCord * randomMeters.toDouble()

        fusedLocationClient.lastLocation.addOnSuccessListener(activity
        ) { locationResult ->
            val currentLong = locationResult?.longitude ?: 0.0
            val currentLat = locationResult?.latitude ?: 0.0

            val coordinates = when (randomPM) {
                0 -> LatLng(currentLat + metersCordN, currentLong + metersCordN)
                1 -> LatLng(currentLat - metersCordN, currentLong - metersCordN)
                2 -> LatLng(currentLat + metersCordN, currentLong - metersCordN)
                3 -> LatLng(currentLat - metersCordN, currentLong + metersCordN)
                4 -> LatLng(currentLat, currentLong - metersCordN)
                else -> LatLng(currentLat - metersCordN, currentLong)
            }

            onSuccess(coordinates)
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