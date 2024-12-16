package com.annoapp.raspberry

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.core.content.ContextCompat
import com.annoapp.raspberry.databinding.ActivityFormulairePlanifBinding
import com.annoapp.raspberry.databinding.ActivityMainBinding

class FormulairePlanifCommandeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val buttonList = mutableListOf<Button>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val buttonFlash: Button = binding.buttonFlash
        val buttonVague: Button = binding.buttonVague
        val buttonPower: Button = binding.buttonPower
        val buttonFull: Button = binding.buttonFull

        
        buttonFlash.backgroundTintList = null
        buttonVague.backgroundTintList = null
        buttonPower.backgroundTintList = null
        buttonFull.backgroundTintList = null

        initializeButtons()

        applySavedButtonColors()

        buttonList.forEach { button ->
            button.backgroundTintList = null
        }

        //set action des boutons
        buttonFlash.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("commande","flash")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        buttonFull.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("commande","full")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        buttonPower.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("commande","power")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        buttonVague.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("commande","vague")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        buttonRainbow.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("commande","arc-en-ciel")
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    // Surcharge pour la creation du menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)
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

    //reprise du code de gabriel
    private fun initializeButtons() {
        buttonList.clear()
        buttonList.addAll(listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9,
            binding.button10,
            binding.button11,
            binding.button12,
            binding.button13,
            binding.button14,
            binding.button15,
            binding.button16,
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
        }
    }

    //reprise du code de gabriel
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

            button.setOnClickListener{
                val resultIntent = Intent()
                resultIntent.putExtra("commande",color)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}