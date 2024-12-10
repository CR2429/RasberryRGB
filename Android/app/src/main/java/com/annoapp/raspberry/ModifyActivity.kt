package com.annoapp.raspberry

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ModifyActivity : AppCompatActivity() {

    private val buttonList = mutableListOf<Button>()
    private var buttonIndex: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modify)

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

        buttonList.forEach { button ->
            button.backgroundTintList = null
            button.setOnClickListener {
                openColorPicker(button)
            }
        }

    }
    override fun onPause() {
        super.onPause()
    }

    // Fonction pour ouvrir le sélecteur de couleurs
    private fun openColorPicker(button: Button) {
        ColorPickerDialog.Builder(this)
            .setTitle("Choisissez une couleur")
            .setPreferenceName("ColorPickerDialog")
            .setPositiveButton("Sélectionner", ColorEnvelopeListener { envelope: ColorEnvelope, _: Boolean ->
                val drawable = ContextCompat.getDrawable(this, R.drawable.button_rond)
                drawable?.setTint(envelope.color)
                button.background = drawable
            })
            .setNegativeButton("Annuler") { dialogInterface, _ -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true) // Afficher la barre de transparence (optionnel)
            .attachBrightnessSlideBar(true) // Afficher la barre de luminosité (optionnel)
            .show()
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
    }




}