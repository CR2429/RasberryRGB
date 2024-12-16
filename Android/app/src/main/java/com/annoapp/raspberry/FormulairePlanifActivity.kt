package com.annoapp.raspberry

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.annoapp.raspberry.databinding.ActivityFormulairePlanifBinding
import java.io.IOException
import java.io.ObjectInputStream
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
                Toast.makeText(this, "Commande : $resultValue", Toast.LENGTH_SHORT).show()
                if (resultValue != null) {
                    thisPlanif.setCommande(resultValue)
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

                    val time = LocalTime.of(hourOfDay, minute)
                    thisPlanif.setHeure(time)
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
            if (thisPlanif.getHeure() == null) {
                Toast.makeText(this,"Il manque une heure", Toast.LENGTH_SHORT).show()
                error = true
            }
            if (thisPlanif.getCommande() == null) {
                Toast.makeText(this, "Il manque une commande", Toast.LENGTH_SHORT).show()
                error = true
            }

            //sauvegarde
            if (!error) {
                thisPlanif.setTitre(binding.EtTitre.text.toString())
                val resultIntent = Intent()
                resultIntent.putExtra("planif",thisPlanif)
                setResult(RESULT_OK, resultIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        //deserialisation
        listePlanif = arrayListOf<Planif>()
        try {
            openFileInput("Planif.data").use {
                ObjectInputStream(it).use {
                    listePlanif = it.readObject() as ArrayList<Planif>
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
}