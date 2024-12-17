package com.annoapp.raspberry

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.annoapp.raspberry.databinding.ActivityPlanifBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class PlanifActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanifBinding
    private lateinit var planifArray: ArrayList<Planif>
    private lateinit var planifAdapter: PlanifAdapter
    private lateinit var BoutonNew: Button
    private lateinit var listView: ListView
    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val resultValue = data?.getParcelableExtra<Planif>("planif")
                if (resultValue != null) {
                    val exists = planifArray.any { it.getID() == resultValue.getID() }
                    if (exists) {
                        val existingPlanif =
                            planifArray.find { it.getID() == resultValue.getID() } as Planif

                        existingPlanif.setTitre(resultValue.getTitre())
                        existingPlanif.setHeure(resultValue.getHeure())
                        existingPlanif.setCommande(resultValue.getCommande())
                    } else {
                        planifArray.add(resultValue)
                    }
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BoutonNew = binding.BNew
        BoutonNew.setOnClickListener {
            newPlanif()
        }
    }

    //dialog de modification de la planification
    fun newPlanif() {
        val intent = Intent(this, FormulairePlanifActivity::class.java)
        intent.putExtra("IsNew", true)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuClose -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //sauvegarde les donnees necessaire
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()

        //serialisation
        try {
            openFileOutput("Planif.json", Context.MODE_PRIVATE).use {
                ObjectOutputStream(it).use {
                    val gson = Gson()
                    val json = gson.toJson(planifArray)
                    it.write(json.toByteArray())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        WorkManager.getInstance(this).cancelAllWork()

        planifArray.forEach() {
            if (it.getActif()) {
                sheduleWork(it)
            }
        }
    }

    //Recupere la sauvegarde
    override fun onResume() {
        super.onResume()

        //deseralisation
        planifArray = arrayListOf<Planif>()
        try {
            openFileInput("Planif.json").use {
                ObjectInputStream(it).use {
                    val gson = Gson()
                    val json = it.bufferedReader().use { it.readText() }
                    val type = object : TypeToken<ArrayList<Planif>>() {}.type
                    planifArray = gson.fromJson(json, type) //il y a une erreur ici
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        listView = binding.LPlanif
        planifAdapter = PlanifAdapter(this, planifArray)
        listView.adapter = planifAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sheduleWork(planif: Planif) {
        val heurePlanifier = planif.getHeure()
        val heureActuelle = LocalTime.now()
        val duration = Duration.between(heureActuelle, heurePlanifier)
        val delayInMillis = duration.toMillis()

        if (delayInMillis > 0) {
            val inputData = Data.Builder()
                .putString("commande", planif.getCommande())
                .build()

            val initialWorkRequest = OneTimeWorkRequest.Builder(PlanifWorker::class.java)
                .setInputData(inputData)
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(this).enqueue(initialWorkRequest)

            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(PlanifWorker::class.java, 24, TimeUnit.HOURS)
                    .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                    .build()

            WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        }
    }
}
