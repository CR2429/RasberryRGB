package com.annoapp.raspberry

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var settings: SharedPreferences
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        val switchNotif: Switch = findViewById(R.id.SNotification)
        if (result) {
            // Permission accordée
            settings.edit().putBoolean("Notif", true).apply()
        } else {
            // Permission refusée
            Toast.makeText(this, "Permission non accordée", Toast.LENGTH_SHORT).show()
            switchNotif.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //recuperer les sharedpreference
        settings = getSharedPreferences("Settings", MODE_PRIVATE)

        //retrouver les element android
        val inputIP: EditText = findViewById(R.id.EtIP)
        val inputPort: EditText = findViewById(R.id.EtPort)
        val switchNotif: Switch = findViewById(R.id.SNotification)

        //mettre les bons reglages
        inputIP.setText(settings.getString("IP", ""))
        inputPort.setText(settings.getString("PORT",""))
        switchNotif.isChecked = settings.getBoolean("Notif",false)

        //interraction
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // Check ON
                val permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                if (permissionStatus == PERMISSION_GRANTED) {
                    settings.edit().putBoolean("Notif", true).apply()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else { // Check OFF
                settings.edit().putBoolean("Notif", false).apply()
            }
        }

    }

    // Surcharge pour la creation du menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Surcharge des interactions des items du menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuClose -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}