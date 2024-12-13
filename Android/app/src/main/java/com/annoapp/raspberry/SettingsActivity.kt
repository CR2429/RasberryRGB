package com.annoapp.raspberry

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception
import java.lang.reflect.Executable

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
        var noErreur = true

        if (inputIP.text.isNullOrEmpty()) {
            Toast.makeText(this,"Vous n'avez pas mis d'addresse IP", Toast.LENGTH_SHORT).show()
            noErreur = false
        }
        if (inputPort.text.isNullOrEmpty()) {
            Toast.makeText(this,"Vous n'avez pas mis de Port", Toast.LENGTH_SHORT).show()
            noErreur = false
        }

        if (noErreur) {
            val ip = inputIP.text.toString()
            val port = inputPort.text.toString()
            val url = "https://${ip}:${port}"
            val reponse = sendGetForTest(url)

            if (reponse==true) {
                Toast.makeText(this, "Connexion reussis", Toast.LENGTH_SHORT).show()
                settings.edit().putString("IP",inputIP.text.toString()).apply()
                settings.edit().putString("PORT",inputPort.text.toString()).apply()
            }
        }

    }
    private fun sendGetForTest(stUrl: String): Boolean? {
        val client = OkHttpClient()
        val request = Request.Builder().url(stUrl).build()

        return try {
            client.newCall(request).execute().use { response: Response ->
                if (!response.isSuccessful) {
                    Log.e("ERREUR", "Erreur de connexion : ${response.code}")
                    Toast.makeText(this, "Erreur de connexion : ${response.code}", Toast.LENGTH_SHORT).show()
                    null
                } else {
                    true//retourne true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ERREUR", e.toString())
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            null
        }
    }
}