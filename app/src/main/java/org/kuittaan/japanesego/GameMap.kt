package org.kuittaan.japanesego

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Random
import java.util.concurrent.TimeUnit

class GameMap {

    @Composable
    fun createMap() {

        val context = LocalContext.current

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

        val styleJson = context.resources.openRawResource(R.raw.stylejson).bufferedReader().use {
            it.readText()
        }

        val mapProperties = MapProperties(
            mapStyleOptions = MapStyleOptions(styleJson)
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties
        ) {

        }

    }

    @Composable
    fun createMarkers() {
        val markerSet = remember { mutableListOf<LatLng>() }
        markerSet.add(generateCoordinates(10, 2000)) // create markers between 10 to 2000 meters away
    }

    @SuppressLint("MissingPermission")
    fun generateCoordinates(minMeters: Int, maxMeters: Int): LatLng {

        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Activity().applicationContext);

        val coordinates: LatLng
        val currentLong: Double
        val currentLat: Double
        val meterCord = 0.00900900900901 / 1000
        //Generate random Meters between the maximum and minimum Meters
        val r = Random()
        val randomMeters: Int = r.nextInt(minMeters+ maxMeters)
        //then Generating Random numbers for different Methods
        val randomPM: Int = r.nextInt(6)

        //Then we convert the distance in meters to coordinates by Multiplying number of meters with 1 Meter Coordinate
        val metersCordN = meterCord * randomMeters.toDouble()
        val locationResult = fusedLocationClient.lastLocation
        currentLong = locationResult.result.longitude
        currentLat= locationResult.result.latitude
        coordinates = when (randomPM) {
            0 -> LatLng(currentLat + metersCordN, currentLong + metersCordN)
            1 -> LatLng(currentLat - metersCordN, currentLong - metersCordN)
            2 -> LatLng(currentLat + metersCordN, currentLong - metersCordN)
            3 -> LatLng(currentLat - metersCordN, currentLong + metersCordN)
            4 -> LatLng(currentLat, currentLong - metersCordN)
            else -> LatLng(currentLat - metersCordN, currentLong)
        }

        return coordinates
    }
}