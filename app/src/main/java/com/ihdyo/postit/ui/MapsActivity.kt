package com.ihdyo.postit.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.ihdyo.postit.R
import com.ihdyo.postit.UserPreferences
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.databinding.ActivityMapsBinding
import com.ihdyo.postit.helper.LocationConverter
import com.ihdyo.postit.viewmodel.DataStoreViewModel
import com.ihdyo.postit.viewmodel.MainViewModel
import com.ihdyo.postit.viewmodel.MainViewModelFactory
import com.ihdyo.postit.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var boundBuilder: LatLngBounds.Builder
    private lateinit var mapsViewModel: MainViewModel
    private lateinit var dataStoreViewModel: DataStoreViewModel
    private lateinit var preferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boundBuilder = LatLngBounds.Builder()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        preferences = UserPreferences.getInstance(dataStore)
        dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))
            .get(DataStoreViewModel::class.java)

        mapsViewModel = ViewModelProvider(this, MainViewModelFactory(this))
            .get(MainViewModel::class.java)

        dataStoreViewModel.getToken().observe(this) { token ->
            mapsViewModel.getStories(token)
        }

        mapsViewModel.stories.observe(this) { stories ->
            if (stories != null) {
                setMarker(stories)
            }
        }

        mapsViewModel.message.observe(this) { message ->
            if (message != "Stories fetched!") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        mapsViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar4.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setMarker(data: List<DataDetail>) {
        var locationZoom: LatLng? = null

        for (item in data) {
            item.lat?.let { lat ->
                item.lon?.let { lon ->
                    val latLng = LatLng(lat, lon)
                    val address = LocationConverter.getStringAddress(latLng, this)
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(item.name)
                            .snippet(address)
                    )
                    marker?.tag = item
                    boundBuilder.include(latLng)

                    if (locationZoom == null) {
                        locationZoom = latLng
                    }
                }
            }
        }

        locationZoom?.let {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 3f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}