package com.shankha.healthtracker

import android.database.Cursor
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shankha.healthtracker.databinding.ActivityOtherUserBinding

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

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsList)
        binding.listView.adapter = adapter

        binding.btnAdd.setOnClickListener {
            val name = binding.sname.text.toString().trim()
            val mobile = binding.smob.text.toString().trim()

            if (name.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Please enter both name and mobile number", Toast.LENGTH_SHORT).show()
            } else {
                val isAdded = dbHelper.addContact(name, mobile)
                if (isAdded) {
                    Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()
                    loadContacts()
                    hideKeyboard()
                    binding.smob.setText("")
                    binding.sname.setText("")
                } else {
                    Toast.makeText(this, "Mobile number already exists", Toast.LENGTH_SHORT).show()
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

            //Toast.makeText(this, "Clicked: $name", Toast.LENGTH_SHORT).show()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        loadContacts()


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
}