package com.dmishra.meteorsonearth.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.dmishra.meteorsonearth.R
import com.dmishra.meteorsonearth.databinding.ActivityMapBinding
import com.dmishra.meteorsonearth.util.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var TAG: String? = null
    private var meteorName: String? = null
    private var lat = 0.0
    private var lng = 0.0
    var actionBar: ActionBar? = null
    var activityMapBinding: ActivityMapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        TAG = this.javaClass.name
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        activityMapBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(activityMapBinding!!.getRoot())
        meteorName = intent.getStringExtra(Constant.Intent.METEORITE_NAME)
        lat = intent.getStringExtra(Constant.Intent.METEORITE_LAT)!!.toDouble()
        lng = intent.getStringExtra(Constant.Intent.METEORITE_LNG)!!.toDouble()
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)
        actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setTitle(meteorName)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val latLng = LatLng(lat, lng)
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(meteorName)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.Map.CAMERA_ZOOM))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }
}
