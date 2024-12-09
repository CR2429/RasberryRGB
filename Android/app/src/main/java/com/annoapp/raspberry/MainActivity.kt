package com.annoapp.raspberry

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorPickerDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val buttonList = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeButtons()


        // Charger les couleurs des boutons
        loadButtonColors()


        // Ajouter des événements de clic pour chaque bouton
        buttonList.forEachIndexed { index, button ->
            button.setOnClickListener {


            }
        }


    }
    override fun onPause() {
        super.onPause()
        saveButtonColors()
    }


    private fun initializeButtons() {
        buttonList.clear()
        buttonList.addAll(listOf(
            findViewById(R.id.button_0),
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
            findViewById(R.id.button_17),
            findViewById(R.id.button_18),
            findViewById(R.id.button_19)
        ))
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