package com.annoapp.raspberry

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.annoapp.raspberry.databinding.ActivityFormulairePlanifBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class FormulairePlanifActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormulairePlanifBinding
    private lateinit var listePlanif: ArrayList<Planif>
    private lateinit var thisPlanif: Planif
    @RequiresApi(Build.VERSION_CODES.O)
    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val resultValue = data?.getStringExtra("commande")
                if (resultValue != null) {
                    Toast.makeText(this, "Commande : $resultValue", Toast.LENGTH_SHORT).show()
                    binding.TvFormCommand.text = resultValue
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormulairePlanifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //bouton pour la commande
        binding.BCommande.setOnClickListener{
            val intent = Intent(this, FormulairePlanifCommandeActivity::class.java)
            startActivityForResult.launch(intent)
        }
        //bouton pour l'heure
        binding.BTime.setOnClickListener {
            // Récupérer l'heure actuelle
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            // Créer le TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Mettre à jour le TextView avec l'heure choisie
                    binding.TvTime.text = String.format("Heure sélectionnée : %02d:%02d", hourOfDay, minute)
                    binding.TvFormHeure.text = hourOfDay.toString()
                    binding.TvFormMinute.text = minute.toString()
                },
                currentHour,
                currentMinute,
                true // Utiliser le format 24 heures
            )
            timePickerDialog.show()
        }
        //bouton de la sauvegarde
        binding.BSauvegarde.setOnClickListener {
            var error = false
            if (binding.EtTitre.text.isEmpty()) {
                Toast.makeText(this,"Il manque un titre", Toast.LENGTH_SHORT).show()
                error = true
            }
            if (binding.TvFormHeure.text.toString() == "") {
                Toast.makeText(this,"Il manque une heure", Toast.LENGTH_SHORT).show()
                error = true
            }
            if (binding.TvFormCommand.text.toString() == "") {
                Toast.makeText(this, "Il manque une commande", Toast.LENGTH_SHORT).show()
                error = true
            }

            //sauvegarde
            if (!error) {
                thisPlanif.setTitre(binding.EtTitre.text.toString())
                thisPlanif.setCommande(binding.TvFormCommand.text.toString())
                val time = LocalTime.of(binding.TvFormHeure.text.toString().toInt(), binding.TvFormMinute.text.toString().toInt())
                thisPlanif.setHeure(time)
                val exists = listePlanif.any { it.getID() == thisPlanif.getID() }
                if (exists) {
                    val existingPlanif = listePlanif.find { it.getID() == thisPlanif.getID() } as Planif

                    existingPlanif.setTitre(thisPlanif.getTitre())
                    existingPlanif.setHeure(thisPlanif.getHeure())
                    existingPlanif.setCommande(thisPlanif.getCommande())
                } else {
                    listePlanif.add(thisPlanif)
                }

                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        //deseralisation
        listePlanif = arrayListOf<Planif>()
        try {
            openFileInput("Planif.json").use {
                ObjectInputStream(it).use {
                    val gson = Gson()
                    val json = it.bufferedReader().use { it.readText() }
                    val type = object : TypeToken<ArrayList<Planif>>() {}.type
                    listePlanif = gson.fromJson(json, type) //il y a une erreur ici
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //modification
        val planifIdIntent = intent.getStringExtra("Planif_id")
        if (!intent.getBooleanExtra("IsNew", false)) {
            for (planif in listePlanif) {
                if (planif.getID() == planifIdIntent) {
                    thisPlanif = planif
                }
            }
        }
        else {
            thisPlanif = Planif(true)
        }

        //mettre les infos dans les inputs
        if(!intent.getBooleanExtra("IsNew",false)) {
            val timeF = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = String.format("Heure sélectionnée : %s", timeF.format(thisPlanif.getHeure()))
            binding.TvTime.setText(time)

            binding.EtTitre.setText(thisPlanif.getTitre())
        }
    }

    //sauvegarde les donnees necessaire
    override fun onPause() {
        super.onPause()

        //serialisation
        try {
            openFileOutput("Planif.json", Context.MODE_PRIVATE).use {
                ObjectOutputStream(it).use {
                    val gson = Gson()
                    val json = gson.toJson(listePlanif)
                    it.write(json.toByteArray())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}