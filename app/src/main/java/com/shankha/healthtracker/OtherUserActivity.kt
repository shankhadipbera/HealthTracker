package com.shankha.healthtracker

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.healthtracker.databinding.ActivityOtherUserBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OtherUserActivity : AppCompatActivity() {
    private val binding: ActivityOtherUserBinding by lazy {
        ActivityOtherUserBinding.inflate(layoutInflater)
    }
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ArrayAdapter<String>
    private val contactsList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dbHelper = DatabaseHelper(this)

        binding.btnanaylisis.isEnabled=false

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsList)
        binding.listView.adapter = adapter

        loadContacts()

        binding.btnAdd.setOnClickListener {
            val name = binding.sname.text.toString().trim()
            val mobile = binding.smob.text.toString().trim()

            if (name.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Please enter both name and User Id", Toast.LENGTH_SHORT).show()
            } else {
                val isAdded = dbHelper.addContact(name, mobile)
                if (isAdded) {
                    Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show()
                    loadContacts()
                    hideKeyboard()
                    binding.smob.setText("")
                    binding.sname.setText("")
                } else {
                    Toast.makeText(this, "User id already exists", Toast.LENGTH_SHORT).show()
                }

            }
        }
        binding.listView.setOnItemLongClickListener { _, _, position, _ ->
            val contact = contactsList[position]
            val name = contact.split(" - ")[0]  // Assuming the format "name - mobile"
            AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete $name?")
                .setPositiveButton("Yes") { _, _ ->
                    if (dbHelper.deleteContact(name)) {
                        Toast.makeText(this, "$name deleted from Save User list", Toast.LENGTH_SHORT).show()
                        loadContacts()
                    } else {
                        Toast.makeText(this, "Error in deleting user", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
            true
        }

            binding.listView.setOnItemClickListener { _, _, position, _ ->

                val selectedContact = contactsList[position]
                val parts = selectedContact.split(" - ")
                val name = parts[0]
                val mobile = parts[1]
                val database = FirebaseDatabase.getInstance().getReference("Users").child(mobile)
                val hDatabase=  FirebaseDatabase.getInstance().getReference("HealthData").child(mobile).child(dateName())
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                           val userData = snapshot.getValue(UserModel::class.java)
                            if(userData!=null){
                                binding.apply {
                                    suname.visibility=View.VISIBLE
                                     sumob.visibility=View.VISIBLE
                                    suname.setText("Name : "+userData.userName.toString())
                                    sumob.setText("Mob. No. : "+userData.phoneNo.toString())
                                }
                                hDatabase.addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            val helthData = snapshot.getValue(HealthData::class.java)
                                            if(helthData!=null){
                                                binding.apply {
                                                    usertemp.setText(helthData.temperature.toString()+ " °C")
                                                    userheart.setText(helthData.heartRate.toString() + " bpm")
                                                    userspo2.setText(helthData.spO2.toString()+ " %")
                                                    stemp.visibility= View.VISIBLE
                                                    sheart.visibility=View.VISIBLE
                                                    savespo2.visibility=View.VISIBLE
                                                    usertemp.visibility=View.VISIBLE
                                                    userheart.visibility=View.VISIBLE
                                                    userspo2.visibility=View.VISIBLE
                                                    btnanaylisis.isEnabled=true

                                                }
                                            }else{
                                               // Toast.makeText(this@OtherUserActivity, "Currently No Data Avaiable", Toast.LENGTH_SHORT).show()
                                            }

                                        }else{
                                            Toast.makeText(this@OtherUserActivity, "Currently No Data Avaiable ", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@OtherUserActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                                    }

                                })
                            }else{
                                Toast.makeText(this@OtherUserActivity, "No Data found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@OtherUserActivity, "No matching user found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@OtherUserActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
                binding.btnanaylisis.setOnClickListener {
                    val intent = Intent(this@OtherUserActivity,AnalysisActivity::class.java)
                    intent.putExtra("userId",mobile)
                    startActivity(intent)
                }
            }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.btnClear.setOnClickListener {
            binding.apply {
                usertemp.setText("")
                userheart.setText("")
                userspo2.setText("")
                suname.setText("")
                sumob.setText("")
                userheart.visibility=View.INVISIBLE
                usertemp.visibility=View.INVISIBLE
                userspo2.visibility=View.INVISIBLE
                suname.visibility=View.INVISIBLE
                sumob.visibility=View.INVISIBLE
                stemp.visibility= View.INVISIBLE
                sheart.visibility=View.INVISIBLE
                savespo2.visibility=View.INVISIBLE
            }
        }

    }
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        currentFocusView?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()

        }
    }


    private fun loadContacts() {
        contactsList.clear()
        val cursor: Cursor = dbHelper.getAllContacts()
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                    val mobile = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOBILE))
                    contactsList.add("$name - $mobile")
                } while (it.moveToNext())
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun dateName():String{
        val simpleDateFormat= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }
}