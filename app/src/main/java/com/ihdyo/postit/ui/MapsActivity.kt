package com.ihdyo.postit.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ihdyo.postit.UserPreferences
import com.ihdyo.postit.viewmodel.DataStoreViewModel
import com.ihdyo.postit.viewmodel.MainViewModel
import com.ihdyo.postit.viewmodel.MainViewModelFactory
import com.ihdyo.postit.viewmodel.ViewModelFactory
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.helper.LocationConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.ihdyo.postit.R
import com.ihdyo.postit.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val boundBuilder = LatLngBounds.Builder()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }
    private val pref by lazy {
        UserPreferences.getInstance(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            mapsViewModel.getStories(it)
        }

        mapsViewModel.stories.observe(this) {
            if (it != null) {
                setMarker(it)
            }
        }

        mapsViewModel.message.observe(this) {
            if (it != "Stories fetched!") Toast.makeText(this, it, Toast.LENGTH_SHORT)
                .show()
        }

        mapsViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar4.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setMarker(data: List<DataDetail>) {
        lateinit var locationZoom: LatLng
        data.forEach {
            if (it.lat != null && it.lon != null) {
                val latLng = LatLng(it.lat, it.lon)
                val address = LocationConverter.getStringAddress(latLng, this)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(it.name)
                        .snippet(address)
                )
                boundBuilder.include(latLng)
                marker?.tag = it

                locationZoom = latLng
            }
        }

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                locationZoom, 3f
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}