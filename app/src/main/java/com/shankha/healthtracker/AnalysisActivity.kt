package com.shankha.healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shankha.healthtracker.databinding.ActivityAnalysisBinding

class AnalysisActivity : AppCompatActivity() {
    private val binding: ActivityAnalysisBinding by lazy {
            ActivityAnalysisBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = intent
        val temp:Float = intent.getFloatExtra("temp", 0.0F)
        val heartRate:Int = intent.getIntExtra("heartR",0)
        val spO2:Int=intent.getIntExtra("spO2",0)
        val age:Int =intent.getIntExtra("Age",0)
        binding.textViewName.text = intent.getStringExtra("Name")
        binding.textViewtemp.text=temp.toString()
        binding.textViewAge.text= age.toString()
        binding.textViewHeartR.text=heartRate.toString()
        binding.textViewO2.text=spO2.toString()

        if(temp in 36.00..37.5 && spO2 in 90..100 && heartRate in 60..100){
            binding.textViewCondition.setText("Normal Condition Everything is ok")
            binding.textViewSympt.setText("Normal , No abnormal Symptoms😊")   // windoews + .
            binding.textViewPrecau.setText("Dont worry you are completly fine☺️👍")
        }
    }

}