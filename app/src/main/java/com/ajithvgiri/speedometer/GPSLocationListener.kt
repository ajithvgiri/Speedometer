package com.ajithvgiri.speedometer

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.provider.Settings

/**
 * Created by ajithvgiri on 15/12/17.
 */

class GPSLocationListener(private var activity: MainActivity) : LocationListener {

    private val TAG = GPSLocationListener::class.java.simpleName
    lateinit var location: Location // location

    override fun onLocationChanged(location: Location) {
        location.latitude
        location.longitude
        val speed :Int = (location.speed * 3600 / 1000).toInt()
        activity.updateUI(location.longitude.toFloat(), location.latitude.toFloat(), speed)
        this.location = location
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {
        activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

}
