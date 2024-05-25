package com.shankha.healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.shankha.healthtracker.databinding.ActivityPersonalDetailsBinding

class PersonalDetailsActivity : AppCompatActivity() {
    private val binding:ActivityPersonalDetailsBinding by lazy{
        ActivityPersonalDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonNext.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val ageInput = binding.editTextAge.text.toString()
            val weightInput = binding.editTextWeight.text.toString()

            val age: Int
            val weight: Int

            if (ageInput.isNotEmpty() && ageInput.isDigitsOnly() &&
                weightInput.isNotEmpty() && weightInput.isDigitsOnly()) {
                age = ageInput.toInt()
                weight = weightInput.toInt()
            } else {
                age = -1
                weight = -1
            }

            if (name.isNotEmpty() && age in 0..100 && weight in 20..150) {
                val intent = Intent(this@PersonalDetailsActivity, MainActivity::class.java)
                intent.putExtra("Name",name)
                intent.putExtra("Age",age)
                intent.putExtra("Weight",weight)

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }










    }
}