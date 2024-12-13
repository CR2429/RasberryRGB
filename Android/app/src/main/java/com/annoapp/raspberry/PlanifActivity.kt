package com.annoapp.raspberry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annoapp.raspberry.databinding.ActivityPlanifBinding
import com.annoapp.raspberry.databinding.ActivitySettingsBinding

class PlanifActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanifBinding
    private lateinit var planifArray: ArrayList<Planif>
    private lateinit var Pla: Planif

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /
    }
}