package com.ajithvgiri.speedometer

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: GPSLocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val font = Typeface.createFromAsset(this.assets, "fonts/digital_7_mono.ttf")
        titleTextView.typeface = font
        longitudeTextView.typeface = font
        latitudeTextView.typeface = font
        speedTextView.typeface = font
        unitsTextView.typeface = font
        longitudeTitle.typeface = font
        latitudeTitle.typeface = font

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = GPSLocationListener(this)

        if (UtilsPermissions(this).checkSelfPermission(this)) {
            getLocationUpdates()
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The LocationPermission is granted to you... Continue your left job...
                getLocationUpdates()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle(getString(R.string.location_permission))
                    builder.setMessage(getString(R.string.permission_description))
                    builder.setPositiveButton(getString(R.string.permission_grant)) { dialog, which ->
                        dialog.cancel()
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                    }
                    builder.setNegativeButton(getString(R.string.permission_cancel)) { dialog, which -> dialog.cancel() }
                    builder.show()
                } else {
                    Toast.makeText(baseContext, getString(R.string.unable_toget_permission), Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                getLocationUpdates()
            }
        }
    }


    override fun onPostResume() {
        super.onPostResume()
        if (UtilsPermissions(this).sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                getLocationUpdates()
            }
        }
    }

    private fun getLocationUpdates() {
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                var criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                criteria.accuracy = Criteria.ACCURACY_FINE


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    return locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2f,
                            locationListener)

                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                }

            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    fun updateUI(longitude:Float,latitude:Float,speed:Int){
        longitudeTextView.text = longitude.toString()
        latitudeTextView.text = latitude.toString()
        speedTextView.text=speed.toString()
    }


    override fun onResume() {
        getLocationUpdates()
        super.onResume()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("speed", speedTextView.text.toString())
        outState.putString("latitude", latitudeTextView.text.toString())
        outState.putString("longitude", longitudeTextView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        speedTextView.text = savedInstanceState.getString("speed")
        latitudeTextView.text = savedInstanceState.getString("latitude")
        longitudeTextView.text = savedInstanceState.getString("longitude")
        super.onRestoreInstanceState(savedInstanceState)
    }

}
