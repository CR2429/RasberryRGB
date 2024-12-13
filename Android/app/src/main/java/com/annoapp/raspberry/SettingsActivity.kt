package com.annoapp.raspberry

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.annoapp.raspberry.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var settings: SharedPreferences
    private lateinit var inputIP: EditText
    private lateinit var inputPort: EditText
    private lateinit var switchNotif: Switch
    private lateinit var connecBouton: Button
    private lateinit var binding: ActivitySettingsBinding
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
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
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //recuperer les sharedpreference
        settings = getSharedPreferences("Settings", MODE_PRIVATE)

        //retrouver les element android
        inputIP = binding.EtIP
        inputPort = binding.EtPort
        switchNotif = binding.SNotification
        connecBouton = binding.BConnexion

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
        connecBouton.setOnClickListener {
            tentativeDeConnection()
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

    //tente de se connecter au rasberry
    fun tentativeDeConnection() {
        //check si les inputs sont remplit de quelque chose
        val noErreur = true

        if (inputIP.text.isNullOrEmpty())
    }
}