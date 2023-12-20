package com.example.hotelfinder

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.Color
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.symbology.SimpleLineSymbol
import com.arcgismaps.mapping.symbology.SimpleLineSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.mapping.view.MapView
import com.example.hotelfinder.data.Hotel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch

class MapView : AppCompatActivity() {

    private val mapView: MapView by lazy {
        findViewById(R.id.mapView)
    }

    private val spinner: Spinner by lazy {
        findViewById(R.id.spinner4)
    }

    private val toolbar: Toolbar by lazy {
        findViewById(R.id.toolbar)
    }

    private val locationDisplay: LocationDisplay by lazy {
        mapView.locationDisplay
    }

    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().getReference()
    }

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        lifecycle.addObserver(mapView)

        setSupportActionBar(toolbar)

        val myadapter = ArrayAdapter<String>(
            this@MapView,
            R.layout.spinner_item,
            resources.getStringArray(R.array.names_spinner)
        )
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = myadapter

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    if (parent.getItemAtPosition(position).toString().equals("Logout")) {
                        FirebaseAuth.getInstance().signOut()
                        val intent : Intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                    if (parent.getItemAtPosition(position).toString().equals("Reservations")) {
                        val intent : Intent = Intent(applicationContext, ReservationHistoryActivity::class.java)
                        startActivity(intent)
                    }
                    if (parent.getItemAtPosition(position).toString().equals("Account")) {
                        val intent : Intent = Intent(applicationContext, Account_change::class.java)
                        startActivity(intent)
                    }
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        setApiKey()

        setupMap()

            placePoints()
    }

    private fun setupMap() {
        val map = ArcGISMap(BasemapStyle.ArcGISTopographic)

        // set the map to be displayed in the layout's MapView
        mapView.map = map

        //mapView.setViewpoint(Viewpoint(34.0270, -118.8050, 72000.0))

        // LocationProvider requires an Android Context to properly interact with Android system
        ArcGISEnvironment.applicationContext = applicationContext
        // set the autoPanMode
        locationDisplay.setAutoPanMode(LocationDisplayAutoPanMode.Recenter)

        lifecycleScope.launch {
            // start the map view's location display
            locationDisplay.dataSource.start()
                .onFailure {
                    // check permissions to see if failure may be due to lack of permissions
                    requestPermissions()
                }
        }
    }

    private fun setApiKey() {
        // It is not best practice to store API keys in source code. We have you insert one here
        // to streamline this tutorial.
        ArcGISEnvironment.apiKey = ApiKey.create("AAPKd4078e3031b1426ebf97df21f4ee78a6yrH-mFDpUhiYurE2MF3l7I2jkeaqC6CFXU_E63Dlni9wa2b9ZMPr7eFvhlS45kmA")
    }

    private fun placePoints() {
        val hotelArrayList = java.util.ArrayList<Hotel>()
        db.collection("hotels").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val hotel = document.toObject(Hotel::class.java)
                        hotelArrayList.add(hotel)
                    }
                    val graphicsOverlay = GraphicsOverlay()
                    mapView.graphicsOverlays.add(graphicsOverlay)

                    for (hotel in hotelArrayList) {
                        // create a point geometry with a location and spatial reference
                        // Point(latitude, longitude, spatial reference)
                        val point = Point(hotel.location.longitude, hotel.location.latitude, SpatialReference.wgs84())

                        // create a point symbol that is an small red circle
                        val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle, Color.red, 15f)

                        // create a blue outline symbol and assign it to the outline property of the simple marker symbol
                        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, Color.fromRgba(0, 0, 255), 3f)
                        simpleMarkerSymbol.outline = blueOutlineSymbol

                        // create a graphic with the point geometry and symbol
                        val pointGraphic = Graphic(point, simpleMarkerSymbol)

                        // add the point graphic to the graphics overlay
                        graphicsOverlay.graphics.add(pointGraphic)
                    }
                } else {
                    Log.w("hotel_info_err", "Error getting documents.", task.exception)
                }
            }
    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        Log.e(localClassName, message)
    }

    private fun requestPermissions() {
        // coarse location permission
        val permissionCheckCoarseLocation =
            ContextCompat.checkSelfPermission(
                this@MapView,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
        // fine location permission
        val permissionCheckFineLocation =
            ContextCompat.checkSelfPermission(
                this@MapView,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED

        // if permissions are not already granted, request permission from the user
        if (!(permissionCheckCoarseLocation && permissionCheckFineLocation)) {
            ActivityCompat.requestPermissions(
                this@MapView,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                2
            )
        } else {
            // permission already granted, so start the location display
            lifecycleScope.launch {
                locationDisplay.dataSource.start()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            // if request is cancelled, the results array is empty
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    locationDisplay.dataSource.start()
                }
            } else {
                val errorMessage = getString(R.string.location_permissions_denied)
                showError(errorMessage)
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent : Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

}
