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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modify)


        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        // Création de 20 boutons dynamiques
        for (i in 0 until 20) {
            val button = Button(this).apply {
                text = ""
                background = ContextCompat.getDrawable(this@ModifyActivity, R.drawable.button_rond)
                setOnClickListener { openColorPicker(this) }
            }

            // Définir les paramètres
            val params = GridLayout.LayoutParams().apply {
                width = 230
                height = 250
                marginStart = 8
                marginEnd = 8
                topMargin = 20

            }
            button.layoutParams = params

            gridLayout.addView(button)
            buttonList.add(button)
        }
        loadButtonColors()

    }
    override fun onPause() {
        super.onPause()
        saveButtonColors()
    }


    // Fonction pour ouvrir le sélecteur de couleurs
    private fun openColorPicker(button: Button) {
        ColorPickerDialog.Builder(this)
            .setTitle("Choisissez une couleur")
            .setPreferenceName("ColorPickerDialog")
            .setPositiveButton("Sélectionner", ColorEnvelopeListener { envelope: ColorEnvelope, _: Boolean ->
                // Appliquer la couleur sélectionnée au bouton
                button.setBackgroundColor(envelope.color)
            })
            .setNegativeButton("Annuler") { dialogInterface, _ -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true) // Afficher la barre de transparence (optionnel)
            .attachBrightnessSlideBar(true) // Afficher la barre de luminosité (optionnel)
            .show()
    }

    private fun saveButtonColors() {
        val sharedPreferences = getSharedPreferences("ButtonColors", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        buttonList.forEachIndexed { index, button ->
            val color = (button.background as? android.graphics.drawable.ColorDrawable)?.color ?: Color.LTGRAY
            editor.putInt("button_$index", color)
        }
        editor.apply()
    }

    private fun loadButtonColors() {
        val sharedPreferences = getSharedPreferences("ButtonColors", MODE_PRIVATE)
        buttonList.forEachIndexed { index, button ->
            val color = sharedPreferences.getInt("button_$index", Color.LTGRAY)
            button.setBackgroundColor(color)
        }
    }

}