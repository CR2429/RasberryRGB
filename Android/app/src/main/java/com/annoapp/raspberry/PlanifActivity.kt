package com.annoapp.raspberry

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.annoapp.raspberry.databinding.ActivityPlanifBinding
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@RequiresApi(Build.VERSION_CODES.O)
class PlanifActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanifBinding
    private lateinit var planifArray: ArrayList<Planif>
    private lateinit var planifAdapter: PlanifAdapter
    private lateinit var BoutonNew: Button
    private lateinit var listView : ListView

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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuClose -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //sauvegarde les donnees necessaire
    override fun onPause() {
        super.onPause()

        //serialisation
        try {
            openFileOutput("Planif.data", Context.MODE_PRIVATE).use {
                ObjectOutputStream(it).use {
                    it.writeObject(planifArray)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Recupere la sauvegarde
    override fun onResume() {
        super.onResume()

        //deseralisation
        planifArray = arrayListOf<Planif>()
        try {
            openFileInput("Planif.data").use {
                ObjectInputStream(it).use {
                    planifArray = it.readObject() as ArrayList<Planif>
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        listView = findViewById<ListView>(R.id.LPlanif)
        planifAdapter = PlanifAdapter(this, planifArray)
        listView.adapter = planifAdapter
    }
}