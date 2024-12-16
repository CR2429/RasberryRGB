package com.annoapp.raspberry

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
import java.text.SimpleDateFormat
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
        binding.BSauvegarde.setOnClickListener{
            val intent = Intent(this, FormulairePlanifCommandeActivity::class.java)
            startActivityForResult.launch(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
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
            val time = timeF.format(thisPlanif.getHeure())
            binding.EtHeure.setText(time)

            binding.EtTitre.setText(thisPlanif.getTitre())
        }
    }
}