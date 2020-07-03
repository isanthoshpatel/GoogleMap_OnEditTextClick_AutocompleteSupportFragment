package com.example.googlemap_autocompletesupportfragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    var pgranted = false
    var location: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var placeClient: PlacesClient? = null
    var autocompleteFragment: AutocompleteSupportFragment? = null
    var q: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, "AIzaSyA5Izem8yLHVPL8UpJjSEMm-b2jYnU3-s0")
        placeClient = Places.createClient(this)


        et.setOnClickListener {
            var i = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            ).build(this)
            startActivityForResult(i, 2)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap.isTrafficEnabled = true
        mMap.isIndoorEnabled = true


        getPermissionsAndLocation()
        if (location != null) {
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        location!!.latitude,
                        location!!.longitude
                    )
                )
            )
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location!!.latitude,
                        location!!.longitude
                    ), 14.0f
                )
            )
        } else {
            mMap.addMarker(MarkerOptions().position(LatLng(21.0, 78.0)))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.0, 78.0), 14.0f))
        }
    }

    fun getPermissionsAndLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pgranted = true
            fusedLocationProviderClient!!.lastLocation.addOnCompleteListener {
                location = it.result
            }
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && requestCode == PackageManager.PERMISSION_GRANTED) {
            pgranted = true
            fusedLocationProviderClient!!.lastLocation.addOnCompleteListener {
                location = it.result
            }
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        }
        if (requestCode == 2 && resultCode == PackageManager.PERMISSION_GRANTED) {
            var places = Autocomplete.getPlaceFromIntent(data!!)
            et.setText(places.address)
            tv.setText(places.name + "" + places.latLng)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        if (pgranted) {
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        location!!.latitude,
                        location!!.longitude
                    )
                )
            )
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location!!.latitude,
                        location!!.longitude
                    ), 14.0f
                )
            )
        } else {
            getPermissionsAndLocation()
        }
        return true
    }


}
