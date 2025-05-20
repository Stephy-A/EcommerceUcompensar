package com.example.ecommerceucompensar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ecommerceucompensar.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest = 1
    private val TAG = "MapsActivity"
    private var isLocationDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLogoutButton()

        try {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            Log.d(TAG, "Map fragment initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing map: ${e.message}")
            Toast.makeText(this, "Error al inicializar el mapa", Toast.LENGTH_SHORT).show()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        // Aquí puedes agregar cualquier lógica de limpieza necesaria
        // Por ejemplo, limpiar preferencias compartidas, tokens, etc.
        
        // Crear un nuevo intent para la actividad de login
        val intent = Intent(this, LoginActivity::class.java)
        
        // Limpiar el stack de actividades
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
        if (!isLocationDialogShown) {
            checkLocationServices()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady called")
        mMap = googleMap
        if (!isLocationDialogShown) {
            checkLocationServices()
        }
    }

    private fun checkLocationServices() {
        Log.d(TAG, "checkLocationServices called")
        try {
            val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
            
            Log.d(TAG, "GPS Enabled: $isGpsEnabled")
            Log.d(TAG, "Network Enabled: $isNetworkEnabled")
            
            if (!isGpsEnabled && !isNetworkEnabled) {
                Log.d(TAG, "Location services are disabled, showing dialog")
                showLocationSettingsDialog()
            } else {
                Log.d(TAG, "Location services are enabled, checking permissions")
                checkLocationPermission()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking location services: ${e.message}")
            Toast.makeText(this, "Error al verificar la ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLocationSettingsDialog() {
        if (isLocationDialogShown) return
        
        Log.d(TAG, "showLocationSettingsDialog called")
        isLocationDialogShown = true
        try {
            AlertDialog.Builder(this)
                .setTitle("Geolocalización Desactivada")
                .setMessage("Para usar esta función, necesitas activar la geolocalización. ¿Deseas activarla ahora?")
                .setPositiveButton("Sí") { _, _ ->
                    Log.d(TAG, "User clicked Yes to enable location")
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, _ ->
                    Log.d(TAG, "User clicked No to enable location")
                    dialog.dismiss()
                    isLocationDialogShown = false
                    Toast.makeText(this, "La geolocalización es necesaria para esta función", Toast.LENGTH_LONG).show()
                    finish() // Cierra la actividad si el usuario no quiere activar la ubicación
                }
                .setCancelable(false)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing location dialog: ${e.message}")
            isLocationDialogShown = false
        }
    }

    private fun checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission called")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Requesting location permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequest
            )
        } else {
            Log.d(TAG, "Location permission already granted")
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation called")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        Log.d(TAG, "Location obtained: ${location.latitude}, ${location.longitude}")
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(currentLatLng)
                                .title("Tu ubicación actual")
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        isLocationDialogShown = false
                    } ?: run {
                        Log.d(TAG, "Location is null")
                        if (!isLocationDialogShown) {
                            Toast.makeText(this, "No se pudo obtener tu ubicación actual", Toast.LENGTH_SHORT).show()
                            checkLocationServices()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting location: ${e.message}")
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult called with requestCode: $requestCode")
        when (requestCode) {
            locationPermissionRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted by user")
                    getCurrentLocation()
                } else {
                    Log.d(TAG, "Permission denied by user")
                    Toast.makeText(
                        this,
                        "Permiso de ubicación denegado",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Cierra la actividad si el usuario deniega el permiso
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        if (!isLocationDialogShown) {
            checkLocationServices()
        }
    }
} 