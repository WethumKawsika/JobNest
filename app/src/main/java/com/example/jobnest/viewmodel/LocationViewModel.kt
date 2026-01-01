package com.example.jobnest.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LocationState(
    val address: String = "",
    val latLng: LatLng? = null
)

class LocationViewModel : ViewModel() {
    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    fun setLocation(address: String, latLng: LatLng) {
        _locationState.value = LocationState(address = address, latLng = latLng)
    }

    fun clearLocation() {
        _locationState.value = LocationState()
    }
}