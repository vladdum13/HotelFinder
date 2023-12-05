package com.example.hotelfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.google.firebase.auth.FirebaseAuth

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
    }

    private fun setupMap() {
        val map = ArcGISMap(BasemapStyle.ArcGISTopographic)

        // set the map to be displayed in the layout's MapView
        mapView.map = map

        mapView.setViewpoint(Viewpoint(34.0270, -118.8050, 72000.0))
    }

    private fun setApiKey() {
        // It is not best practice to store API keys in source code. We have you insert one here
        // to streamline this tutorial.
        ArcGISEnvironment.apiKey = ApiKey.create("AAPKd4078e3031b1426ebf97df21f4ee78a6yrH-mFDpUhiYurE2MF3l7I2jkeaqC6CFXU_E63Dlni9wa2b9ZMPr7eFvhlS45kmA")
    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        Log.e(localClassName, message)
    }
}
