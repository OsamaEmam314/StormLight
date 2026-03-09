package com.example.stormlight.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class LatLon(val lat: Double, val lon: Double)

object LocationHelper {


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context): Flow<LatLon?> = callbackFlow {
        val client = LocationServices.getFusedLocationProviderClient(context)
        fun fetchFreshLocation() {
            val req = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()
            client.getCurrentLocation(req, null)
                .addOnSuccessListener { location ->
                    trySend(location?.let { LatLon(it.latitude, it.longitude) })
                    close()
                }
                .addOnFailureListener {
                    trySend(null)
                    close()
                }
        }

        client.lastLocation
            .addOnSuccessListener { lastLocation ->
                val tenMinutesInMillis = 10 * 60 * 1000
                val isLocationFresh = lastLocation != null &&
                        (System.currentTimeMillis() - lastLocation.time) < tenMinutesInMillis

                if (isLocationFresh) {
                    trySend(LatLon(lastLocation.latitude, lastLocation.longitude))
                    close()
                } else {
                    fetchFreshLocation()
                }
            }
            .addOnFailureListener {
                fetchFreshLocation()
            }

        awaitClose()
    }
    fun isLocationEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}