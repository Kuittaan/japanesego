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
}