package com.shankha.healthtracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.shankha.healthtracker.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private val binding: ActivityProfileBinding by lazy{
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private lateinit var  auth: FirebaseAuth
    private lateinit var  database: DatabaseReference
    private lateinit var userModel: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth= Firebase.auth
        database= Firebase.database.reference
        if(auth.currentUser!=null){
            val userId= auth.currentUser!!.uid
            loadUserData(userId)
        }

        disableClick()
        binding.backIng.setOnClickListener {
            finish()
        }
        binding.editPic.setOnClickListener {
            enableClick()
        }
        binding.editText.setOnClickListener {
            enableClick()
        }
        binding.btnSave.setOnClickListener {
            val name = binding.ename.text.toString().trim()
            val mob = binding.eMob.text.toString().trim()
            val age = binding.eage.text.toString().trim()
            val weight = binding.eweight.text.toString().trim()
            updateUserData(name, mob, age, weight)
            disableClick()
        }


    }

    private fun updateUserData(name: String, mob: String, age: String, weight: String) {
        val userId =auth.currentUser?.uid
        if(userId!=null){
            val userReference =database.child("Users").child(userId)
            val userData= hashMapOf(
                "userName" to name,
                "weight" to weight,
                "age" to age,
                "phoneNo" to mob
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
                // auth.currentUser?.updateEmail(email)

            }.addOnFailureListener {
                Toast.makeText(this,"Failed to update Profile  \n Please try again",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableClick() {
        binding.editText.isEnabled=false
        binding.editPic.isEnabled=false
        binding.tLEname.isEnabled=true
        binding.tLEmob.isEnabled=true
        binding.tLEweight.isEnabled=true
        binding.tLEage.isEnabled=true
        binding.btnSave.isEnabled=true

    }

    private fun loadUserData(userId: String) {
        database.child("Users").child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userModel = snapshot.getValue(UserModel::class.java)!!
                    if(userModel!=null){
                        binding.ename.setText(userModel.userName.toString())
                        binding.eage.setText(userModel.age.toString())
                        binding.eweight.setText(userModel.weight.toString())
                        binding.eMob.setText(userModel.phoneNo.toString())
                    }else{
                        Toast.makeText(this@ProfileActivity,"Failed to Fetch Data", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@ProfileActivity,"No Data Available", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun disableClick() {
        binding.tLEname.isEnabled=false
        binding.tLEmob.isEnabled=false
        binding.tLEweight.isEnabled=false
        binding.tLEage.isEnabled=false
        binding.btnSave.isEnabled=false
        binding.editPic.isEnabled=true
        binding.editText.isEnabled=true
    }
}