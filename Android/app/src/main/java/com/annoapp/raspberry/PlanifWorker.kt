package com.annoapp.raspberry

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PlanifWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        //tache
        val commande = inputData.getString("commande")

        //commande
        if (commande == "toggle") {
            sendPostRequest(toggle = true)
        } else if (commande == "flash") {
            sendPostRequest(modeThread = "flash")
        } else if (commande == "vague") {
            sendPostRequest(modeThread = "vague")
        } else if (commande == "full") {
            sendPostRequest(modeThread = "full")
        } else {
            sendPostRequest(color = commande)
        }

        return Result.success()
    }

    //reprise du code de grabriel
    private fun sendPostRequest(toggle: Boolean? = null, modeThread: String? = null, color: String? = null) {
        val jsonParam = JSONObject()

        val settings = applicationContext.getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        val ip = settings.getString("IP", "")
        val port = settings.getString("PORT", "")

        // Vérifier si l'IP et le port sont valides
        if (ip.isNullOrEmpty() || port.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "L'adresse IP ou le port est manquant", Toast.LENGTH_SHORT).show()
            return
        }

        // URL Beeceptor
        val url = "http://${ip}:${port}"

        // Ajouter des champs dynamiques au JSON
        toggle?.let { jsonParam.put("toggle", it) }
        modeThread?.let { jsonParam.put("mode", it) }
        color?.let {
            val (r, g, b) = hexToRgb(it)
            jsonParam.put("current_color", JSONObject().apply {
                put("r", r)
                put("g", g)
                put("b", b)
            })
        }

        Log.d("Request JSON", jsonParam.toString())

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
                showNotification("Changement appliqué", "Changement effectué avec succès")
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("Error Response", "Code: $responseCode, Error: $errorStream")
                showNotification("Erreur", "Code: $responseCode\nError: $errorStream")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Erreur", "Erreur lors de la requête : ${e.message}")
            Toast.makeText(applicationContext, "Erreur lors de la requête : ${e.message}", Toast.LENGTH_SHORT).show()
            showNotification("Erreur", "Erreur lors de la requête : ${e.message}")
        }
    }

    //reprise du code de gabriel
    private fun hexToRgb(hexColor: String): Triple<Int, Int, Int> {
        val color = Color.parseColor(hexColor)  // Convert hex color string to a Color object
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Triple(red, green, blue)  // Return as a Triple of RGB values
    }

    //reprise du code de gabriel
    private fun showNotification(title: String, message: String) {
        createNotificationChannel()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendNotification(title, message)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                applicationContext as AppCompatActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        } else {
            sendNotification(title, message)
        }
    }

    //reprise du code de gabriel
    private fun sendNotification(title: String, message: String) {
        val channelId = "post_request_channel"
        val notificationId = 1

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.pencil_edit_button)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        // Afficher la notification
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

                // If the permission is not granted, request it
                ActivityCompat.requestPermissions(
                    applicationContext as AppCompatActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101  // You can use a custom request code
                )
                return
            }

            notify(notificationId, builder.build())
        }
    }

    //reprise du code de gabriel
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
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
