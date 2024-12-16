package com.annoapp.raspberry

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.widget.GridLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorPickerDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private val buttonList = mutableListOf<Button>()
    

    // Surcharge pour la creation du menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Surcharge des interactions des items du menu
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.menuModifier -> {
                val intent = Intent(this, ModifyActivity::class.java)
                startActivity(intent)
            }
            R.id.menuPlanifier -> {
                val intent = Intent(this, PlanifActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()


        val buttonFlash: Button = findViewById(R.id.button_flash)
        val buttonVague: Button = findViewById(R.id.button_vague)
        val buttonPower: Button = findViewById(R.id.button_power)
        val buttonFull: Button = findViewById(R.id.button_full)

        buttonFlash.backgroundTintList = null
        buttonVague.backgroundTintList = null
        buttonPower.backgroundTintList = null
        buttonFull.backgroundTintList = null

        buttonPower.setOnClickListener {
            sendPostRequest(toggle = true) // Allume
        }

        buttonFlash.setOnClickListener {
            sendPostRequest(modeThread = "flash")
        }

        buttonVague.setOnClickListener {
            sendPostRequest(modeThread = "vague")
        }

        buttonFull.setOnClickListener {
            sendPostRequest(modeThread = "full")
        }

        initializeButtons()

        applySavedButtonColors()



        buttonList.forEach { button ->
            button.backgroundTintList = null
        }


    }

    override fun onResume() {
        super.onResume()
        applySavedButtonColors()

    }
    override fun onPause() {
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now show the notification
                sendNotification("Permission Granted", "You can now receive notifications.")
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "post_request_channel"  // Use the same ID as in sendNotification
            val channelName = "LED State Notifications"
            val descriptionText = "Notifications for the LED state"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "post_request_channel"
        val notificationId = 1

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.pencil_edit_button)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        // Afficher la notification
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

                // If the permission is not granted, request it
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101  // You can use a custom request code
                )
                return
            }

            notify(notificationId, builder.build())

        }
    }

    private fun showNotification(title: String, message: String) {
        createNotificationChannel()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendNotification(title, message)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        } else {
            sendNotification(title, message)
        }
    }

    private fun initializeButtons() {
        buttonList.clear()
        buttonList.addAll(listOf(
            findViewById(R.id.button_1),
            findViewById(R.id.button_2),
            findViewById(R.id.button_3),
            findViewById(R.id.button_4),
            findViewById(R.id.button_5),
            findViewById(R.id.button_6),
            findViewById(R.id.button_7),
            findViewById(R.id.button_8),
            findViewById(R.id.button_9),
            findViewById(R.id.button_10),
            findViewById(R.id.button_11),
            findViewById(R.id.button_12),
            findViewById(R.id.button_13),
            findViewById(R.id.button_14),
            findViewById(R.id.button_15),
            findViewById(R.id.button_16),
        ))

        // Couleurs par défaut généré par chatgpt
        val colors = listOf(
            Color.rgb(0, 180, 182),  // Cyan (Button 1)
            Color.rgb(122, 125, 255), // Light Blue (Button 2)
            Color.rgb(209, 109, 255), // Purple (Button 3)
            Color.rgb(75, 80, 162),   // Dark Blue (Button 4)
            Color.rgb(240, 97, 200),  // Pink (Button 5)
            Color.rgb(209, 169, 241), // Lavender (Button 6)
            Color.rgb(255, 211, 46),  // Yellow (Button 7)
            Color.rgb(255, 87, 34),   // Orange (Button 8)
            Color.rgb(227, 26, 91),   // Red (Button 9)
            Color.rgb(155, 27, 49),   // Dark Red (Button 10)
            Color.rgb(90, 159, 85),   // Green (Button 11)
            Color.rgb(62, 93, 77),    // Olive (Button 12)
            Color.rgb(139, 69, 19),   // Brown (Button 13)
            Color.rgb(48, 48, 48),    // Dark Grey (Button 14)
            Color.rgb(106, 78, 35),   // Khaki (Button 15)
            Color.rgb(47, 79, 79)     // Dark Green (Button 16)
        )

        buttonList.forEachIndexed { index, button ->
            button.background = ContextCompat.getDrawable(this, R.drawable.button_rond)
            button.backgroundTintList = ColorStateList.valueOf(colors[index])


            // Ajouter un listener pour chaque bouton
            button.setOnClickListener {
                val hexColor = String.format("#%06X", 0xFFFFFF and colors[index]) // Convertir la couleur en hexadécimal
                sendPostRequest(color = hexColor) // Envoyer la couleur au serveur
            }
        }
    }

    private fun hexToRgb(hexColor: String): Triple<Int, Int, Int> {
        val color = Color.parseColor(hexColor)  // Convert hex color string to a Color object
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Triple(red, green, blue)  // Return as a Triple of RGB values
    }

    private fun applySavedButtonColors() {
        val sharedPreferences = getSharedPreferences("ButtonColors", MODE_PRIVATE)

        buttonList.forEach { button ->
            val buttonId = resources.getResourceEntryName(button.id)

            // Récupérer la couleur enregistrée sous forme d'entier
            val color = sharedPreferences.getInt(buttonId, Color.TRANSPARENT)

            if (color != Color.TRANSPARENT) {
                val drawable = ContextCompat.getDrawable(this, R.drawable.button_rond)
                drawable?.setTint(color)
                button.background = drawable
            }
        }
    }

    private fun sendPostRequest(toggle: Boolean? = null, modeThread: String? = null, color: String? = null) {
        val jsonParam = JSONObject()

        // Ajouter des champs dynamiques au JSON
        toggle?.let { jsonParam.put("toggle", it) }
        modeThread?.let { jsonParam.put("mode_thread", it) }
        color?.let {
            val (r, g, b) = hexToRgb(it)
            jsonParam.put("current_color", JSONObject().apply {
                put("r", r)
                put("g", g)
                put("b", b)
            })
        }

        Log.d("Request JSON", jsonParam.toString())

        // URL Beeceptor
        val url = "https://ledrgb.free.beeceptor.com/test"

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }

            // Envoyer le JSON
            connection.outputStream.use { outputStream ->
                outputStream.write(jsonParam.toString().toByteArray())
                outputStream.flush()
            }

            // Lire la réponse
            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }
            Log.i("Response", "Code: $responseCode, Message: $responseMessage")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                showNotification("Requête réussie", "Action effectuée avec succès")
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("Error Response", "Code: $responseCode, Error: $errorStream")
                showNotification("Erreur", "Code: $responseCode\nError: $errorStream")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Erreur", "Erreur lors de la requête : ${e.message}")
            Toast.makeText(this, "Erreur lors de la requête : ${e.message}", Toast.LENGTH_SHORT).show()
            showNotification("Erreur", "Erreur lors de la requête : ${e.message}")
        }
    }





}