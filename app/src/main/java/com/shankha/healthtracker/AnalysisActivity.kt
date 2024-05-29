package com.shankha.healthtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.shankha.healthtracker.databinding.ActivityAnalysisBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalysisActivity : AppCompatActivity() {
    private val binding: ActivityAnalysisBinding by lazy {
            ActivityAnalysisBinding.inflate(layoutInflater)
    }

    private lateinit var  auth: FirebaseAuth
    private lateinit var  database: DatabaseReference
    //private lateinit var healthData:HealthData
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= Firebase.auth
        database= Firebase.database.reference

        binding.buttonBack.setOnClickListener {
            finish()
        }

      /*  val intent = intent
        val temp:Float = intent.getFloatExtra("temp", 0.0F)
        val heartRate:Int = intent.getIntExtra("heartR",0)
        val spO2:Int=intent.getIntExtra("spO2",0)
        val age:Int =intent.getIntExtra("Age",0)
        binding.textViewName.text = intent.getStringExtra("Name")
        binding.textViewtemp.text=temp.toString()
        binding.textViewAge.text= age.toString()
        binding.textViewHeartR.text=heartRate.toString()
        binding.textViewO2.text=spO2.toString()  */

            if(auth.currentUser!=null){
                val userId= auth.currentUser!!.uid
                 var temp:Float
                var heartRate:Int
                 var spO2:Int
                database.child("HealthData").child(userId).child(dateName()).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val healthData = snapshot.getValue(HealthData::class.java)
                            if(healthData!=null){
                                Log.d("firebase", "HealthData: $healthData")
                                binding.textViewtemp.setText(healthData.temperature.toString())
                                binding.textViewHeartR.setText(healthData.heartRate.toString())
                                binding.textViewO2.setText(healthData.spO2.toString())
                                 temp = healthData.temperature.toFloat()
                                 heartRate = healthData.heartRate.toInt()
                                 spO2= healthData.spO2.toInt()
                                if(temp in 36.00..40.0 && spO2 in 90..100 && heartRate in 60..100){
                                    binding.textViewCondition.setText("Normal Condition Everything is ok")
                                    binding.textViewSympt.setText("Normal , No abnormal Symptoms😊")   // windoews + .
                                    binding.textViewPrecau.setText("Dont worry you are completly fine☺️👍")
                                }
                            }else{
                                Log.e("firebase", "HealthData is null: $snapshot")
                                Toast.makeText(this@AnalysisActivity,"Failed to Fetch Data",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@AnalysisActivity,"No Data Available",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("firebase", "Error getting data", error.toException())
                    }

                })
                database.child("Users").child(userId).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            userModel = snapshot.getValue(UserModel::class.java)!!
                            if(userModel!=null){
                                binding.textViewName.setText(userModel.userName.toString())
                                binding.textViewAge.setText(userModel.age.toString())
                            }else{
                                Toast.makeText(this@AnalysisActivity,"Failed to Fetch Data",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@AnalysisActivity,"No Data Available",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            }


    }
    fun dateName():String{
        val simpleDateFormat= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }

}