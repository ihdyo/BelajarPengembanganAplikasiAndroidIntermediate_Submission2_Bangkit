package com.ihdyo.postit.helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.lang.StringBuilder

@Suppress("DEPRECATION")
class LocationConverter {
    companion object {
        fun toLatlng(lat: Double?, lng: Double?): LatLng? {
            return if (lat != null && lng != null) {
                LatLng(lat, lng)
            } else null
        }

        fun getStringAddress(latlng: LatLng?, context: Context): String {
            var fullAddress = "No Location"

            try {
                if (latlng != null) {
                    val geocoder = Geocoder(context)
                    val addresses: List<Address> =
                        geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1) as List<Address>

                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.locality
                        val state = address.adminArea
                        val country = address.countryName

                        fullAddress = address.getAddressLine(0)
                            ?: if (city != null && state != null && country != null) {
                                "$city, $state, $country"
                            } else if (state != null && country != null) {
                                "$state, $country"
                            } else {
                                country ?: "Unknown location name"
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return fullAddress
        }
    }
}