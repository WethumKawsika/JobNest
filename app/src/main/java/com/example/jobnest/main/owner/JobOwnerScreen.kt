package com.example.jobnest.main.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng

const val OWNER_DASHBOARD_ROUTE = "owner_dashboard"
const val POST_JOB_ROUTE = "post_job"
const val MAP_PICKER_ROUTE = "map_picker"

@Composable
fun JobOwnerScreen(rootNavController: NavController, onSwitchView: () -> Unit) {
    val ownerNavController = rememberNavController()
    var selectedAddress by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    NavHost(navController = ownerNavController, startDestination = OWNER_DASHBOARD_ROUTE) {
        composable(OWNER_DASHBOARD_ROUTE) {
            OwnerDashboardScreen(
                onPostJobClick = { ownerNavController.navigate(POST_JOB_ROUTE) },
                onSwitchView = onSwitchView
            )
        }
        composable(POST_JOB_ROUTE) {
            PostJobScreen(
                onBackPressed = { ownerNavController.popBackStack() },
                onPostJob = { ownerNavController.popBackStack() },
                onOpenMapPicker = { ownerNavController.navigate(MAP_PICKER_ROUTE) },
                selectedAddress = selectedAddress,
                selectedLatLng = selectedLatLng
            )
        }
        composable(MAP_PICKER_ROUTE) {
            MapPickerScreen(
                onLocationSelected = { address, latLng ->
                    selectedAddress = address
                    selectedLatLng = latLng
                    ownerNavController.popBackStack()
                },
                onBackPressed = { ownerNavController.popBackStack() }
            )
        }
    }
}
