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

    private lateinit var buttonList: List<Button>
    private var buttonIndex: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modify)

        buttonIndex = intent.getIntExtra("button_index", -1)

        buttonList = (1..16).map { index ->
            findViewById<Button>(resources.getIdentifier("button_$index", "id", packageName))
        }

        buttonList.forEach { button ->
            button.setOnClickListener {
                openColorPicker(button)
            }
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