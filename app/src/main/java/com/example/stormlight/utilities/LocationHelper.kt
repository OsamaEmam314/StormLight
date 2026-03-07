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
                    val req = CurrentLocationRequest.Builder()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build()
                    client.getCurrentLocation(req, null)
                        .addOnSuccessListener {
                            trySend(it?.let { LatLon(it.latitude, it.longitude) })
                            close()
                        }
                        .addOnFailureListener {
                            trySend(null)
                            close()
                        }
        awaitClose()

    }
    fun isLocationEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}