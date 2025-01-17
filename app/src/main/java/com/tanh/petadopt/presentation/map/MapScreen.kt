package com.tanh.petadopt.presentation.map

import android.content.pm.PackageManager
import android.util.Log
import android.webkit.WebSettings.ZoomDensity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.google.android.gms.location.LocationServices
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.tanh.petadopt.R
import com.tanh.petadopt.presentation.OneTimeEvent
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel? = null,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel?.state?.collectAsState(initial = MapUiState())?.value ?: MapUiState()

    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var currentLongitude by remember {
        mutableDoubleStateOf(105.8125165 )
    }

    var currentLatitude by remember {
        mutableDoubleStateOf(20.9844803)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(5.0)
            center(
                Point.fromLngLat(currentLongitude, currentLatitude)
            )
        }
    }

    val longitudeState = MutableStateFlow(currentLongitude).collectAsState().value
    val latitudeState = MutableStateFlow(currentLatitude).collectAsState().value

    LaunchedEffect(true) {
        viewModel?.channel?.collect { event ->
            when (event) {
                is OneTimeEvent.Navigate -> {
                    onNavigate(event)
                }

                is OneTimeEvent.ShowSnackbar -> TODO()
                is OneTimeEvent.ShowToast -> TODO()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel?.initPetList()
    }

    LaunchedEffect(Unit) {
        if(ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(location != null) {
                    currentLongitude = location.longitude
                    currentLatitude = location.latitude
                    mapViewportState.setCameraOptions {
                        center(
                            Point.fromLngLat(
                                location.longitude,
                                location.latitude
                            )
                        )
                    }
                    Log.d("mapp", "current: $currentLongitude $currentLatitude")
                } else {
                    Log.d("mapp", "cannot access permission")
                }
            }
        }
    }

    if(state.isLoading) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else {
        MapboxMap(
            modifier = modifier.fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(5.0)
                    center(
                        Point.fromLngLat(
                            longitudeState,
                            latitudeState
                        )
                    )
                }
            }
        ) {
            val marker = rememberIconImage(
                key = "marker",
                painter = painterResource(id = R.drawable.red_marker)
            )
            PointAnnotation(
                point = Point.fromLngLat(currentLongitude, currentLatitude)
            ) {
                iconImage = marker
            }
            state.pets.forEach { pet ->
                Log.d("mapp", pet.name +pet.longitude.toString() + "\n")
                if (pet.longitude != null && pet.latitude != null) {
                    ViewAnnotation(
                        options = viewAnnotationOptions {
                            geometry(Point.fromLngLat(pet.longitude, pet.latitude))
                        }
                    ) {
                        CustomMarker(
                            modifier = Modifier.clickable {
                                viewModel?.navToDetailPet(pet.animalId ?: "")
                            },
                            pet = pet
                        )
                    }
                }
            }
        }
    }

}