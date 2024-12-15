package com.annoapp.raspberry

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorPickerDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private val buttonList = mutableListOf<Button>()
    

    // Surcharge pour la creation du menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Surcharge des interactions des items du menu
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
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val buttonRainbow: Button = findViewById(R.id.button_rainbow)
        val buttonFlash: Button = findViewById(R.id.button_flash)
        val buttonVague: Button = findViewById(R.id.button_vague)
        val buttonPower: Button = findViewById(R.id.button_power)
        val buttonFull: Button = findViewById(R.id.button_full)

        buttonRainbow.backgroundTintList = null
        buttonFlash.backgroundTintList = null
        buttonVague.backgroundTintList = null
        buttonPower.backgroundTintList = null
        buttonFull.backgroundTintList = null

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
        }
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


}