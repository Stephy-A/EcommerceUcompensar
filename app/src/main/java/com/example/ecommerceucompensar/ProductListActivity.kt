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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceucompensar.databinding.ActivityProductListBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest = 1
    private val TAG = "ProductListActivity"
    private var isLocationDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupCartButton()
        setupLocationButton()
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupLocationButton() {
        binding.btnLocation.setOnClickListener {
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
                // Check if location services are enabled
                val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                val isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)

                if (!isGpsEnabled && !isNetworkEnabled) {
                    Log.d(TAG, "Location services are disabled")
                    showLocationSettingsDialog()
                    return
                }

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            Log.d(TAG, "Location obtained: ${location.latitude}, ${location.longitude}")
                            Toast.makeText(this, 
                                "Tu ubicación actual: ${location.latitude}, ${location.longitude}", 
                                Toast.LENGTH_LONG).show()
                            isLocationDialogShown = false
                        } ?: run {
                            Log.d(TAG, "Location is null, requesting location updates")
                            // If location is null, try to get location updates
                            if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val locationRequest = com.google.android.gms.location.LocationRequest.Builder(10000)
                                    .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                                    .setMinUpdateIntervalMillis(5000)
                                    .build()

                                fusedLocationClient.requestLocationUpdates(
                                    locationRequest,
                                    object : com.google.android.gms.location.LocationCallback() {
                                        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                            val location = locationResult.lastLocation
                                            if (location != null) {
                                                Log.d(TAG, "Location obtained from updates: ${location.latitude}, ${location.longitude}")
                                                Toast.makeText(this@ProductListActivity, 
                                                    "Tu ubicación actual: ${location.latitude}, ${location.longitude}", 
                                                    Toast.LENGTH_LONG).show()
                                                fusedLocationClient.removeLocationUpdates(this)
                                                isLocationDialogShown = false
                                            }
                                        }
                                    },
                                    null
                                )
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error getting location: ${e.message}")
                        Toast.makeText(this, "Error al obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting location: ${e.message}")
                Toast.makeText(this, "Error al obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Location permission not granted")
            checkLocationPermission()
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
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val products = listOf(
            Product(
                id = 1,
                name = "Smartphone",
                description = "Último modelo con cámara de alta resolución",
                price = 999.99,
                imageResId = R.drawable.smartphone
            ),
            Product(
                id = 2,
                name = "Laptop",
                description = "Procesador de última generación",
                price = 1299.99,
                imageResId = R.drawable.laptop
            ),
            Product(
                id = 3,
                name = "Smart TV",
                description = "4K HDR con Android TV",
                price = 799.99,
                imageResId = R.drawable.smartv
            ),
            Product(
                id = 4,
                name = "Auriculares Inalámbricos",
                description = "Cancelación de ruido activa",
                price = 199.99,
                imageResId = R.drawable.auriculares
            ),
            Product(
                id = 5,
                name = "Smartwatch",
                description = "Monitoreo de salud y fitness",
                price = 299.99,
                imageResId = R.drawable.smartwatch
            )
        )

        adapter = ProductAdapter(products) { product ->
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = this@ProductListActivity.adapter
        }
    }

    private fun setupCartButton() {
        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
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