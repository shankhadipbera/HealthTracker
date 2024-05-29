package com.shankha.healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.shankha.healthtracker.databinding.ActivityPersonalDetailsBinding

class PersonalDetailsActivity : AppCompatActivity() {
    private val binding:ActivityPersonalDetailsBinding by lazy{
        ActivityPersonalDetailsBinding.inflate(layoutInflater)
    }

    private lateinit var  auth: FirebaseAuth
    private lateinit var  database: DatabaseReference
    private lateinit var  userName: String
    private lateinit var  email:String
    private lateinit var  password :String
    private lateinit var ageInput:String
    private lateinit var weightInput:String
    private lateinit var mobileNo:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= Firebase.auth
        database= Firebase.database.reference

        binding.textSignIn.setOnClickListener{
            binding.textLEmail.visibility= View.VISIBLE
            binding.textLPassword.visibility= View.VISIBLE
            binding.btnSignIn.visibility= View.VISIBLE
            binding.textSignUp.visibility=View.INVISIBLE
            binding.textSignIn.visibility=View.INVISIBLE
            binding.btnSignUp.visibility=View.INVISIBLE
            binding.textLName.visibility= View.INVISIBLE
            binding.textLAge.visibility= View.INVISIBLE
            binding.textLWeight.visibility= View.INVISIBLE
            binding.textLMobile.visibility=View.INVISIBLE

        }
        binding.textSignUp.setOnClickListener {
            binding.textLEmail.visibility= View.VISIBLE
            binding.textLPassword.visibility= View.VISIBLE
            binding.btnSignUp.visibility= View.VISIBLE
            binding.textSignUp.visibility=View.INVISIBLE
            binding.textSignIn.visibility=View.INVISIBLE
            binding.btnSignIn.visibility=View.INVISIBLE
            binding.textLName.visibility= View.VISIBLE
            binding.textLAge.visibility= View.VISIBLE
            binding.textLWeight.visibility= View.VISIBLE
            binding.textLMobile.visibility=View.VISIBLE
        }

        binding.btnSignIn.setOnClickListener {

        }

        binding.btnSignIn.setOnClickListener {
            email = binding.emailId.text.toString().trim()
            password = binding.password.text.toString().trim()
            if(email.isNotEmpty()&&password.isNotEmpty()){
                loginUser(email,password)
            }else{
                Toast.makeText(this,"Please fill all the details",Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignUp.setOnClickListener {
             userName = binding.name.text.toString().trim()
             ageInput = binding.age.text.toString().trim()
             weightInput = binding.weight.text.toString().trim()
            mobileNo = binding.mobileNo.text.toString().trim()
             email = binding.emailId.text.toString().trim()
             password = binding.password.text.toString().trim()

            if (ageInput.isNotEmpty() && ageInput.isDigitsOnly() &&
                weightInput.isNotEmpty() && weightInput.isDigitsOnly()&&
                userName.isNotEmpty() && mobileNo.isNotEmpty() &&
                email.isNotEmpty()) {

                val age = ageInput.toInt()
                val weight= weightInput.toInt()
                if(age in 0..100 && weight in 20..150 && userName.isNotEmpty()){
                    createAccount(email,password,mobileNo,userName,ageInput,weightInput)
                   /* val intent = Intent(this@PersonalDetailsActivity, MainActivity::class.java)
                    intent.putExtra("Name",userName)
                    intent.putExtra("Age",age)
                    intent.putExtra("Weight",weight)
                    startActivity(intent)
                    finish()  */
                }else{
                    Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this,"Please fill all the details",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(email: String, password: String, mobileNo:String, name:String,age:String,weight:String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                saveUserData(email,password,mobileNo,name,age,weight)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
            if(task.isSuccessful){
               // val user = auth.currentUser
                startActivity(Intent(this,MainActivity::class.java))
                finish()
                Toast.makeText(this,"Login Successful", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveUserData(email: String, password: String, mobileNo:String, name:String,age:String,weight:String) {
        val user=UserModel(name,email,password,mobileNo,age,weight)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
        database.child("Users").child(userId).setValue(user)
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}