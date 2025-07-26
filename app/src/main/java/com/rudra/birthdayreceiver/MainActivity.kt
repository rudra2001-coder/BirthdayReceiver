// MainActivity.kt
package com.rudra.birthdayreceiver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var pickDateBtn: Button
    private lateinit var pickImageBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var previewImage: ImageView
    private lateinit var birthdayText: TextView

    private var selectedDate: String = ""
    private var selectedImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickDateBtn = findViewById(R.id.pickDateBtn)
        pickImageBtn = findViewById(R.id.pickImageBtn)
        saveBtn = findViewById(R.id.saveBtn)
        resetBtn = findViewById(R.id.resetBtn)
        previewImage = findViewById(R.id.previewImage)
        birthdayText = findViewById(R.id.birthdayText)

        pickDateBtn.setOnClickListener { showDatePicker() }
        pickImageBtn.setOnClickListener { pickImage() }
        saveBtn.setOnClickListener { saveData() }
        resetBtn.setOnClickListener { clearData() }

        loadSavedData()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = android.app.DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val date = "$dayOfMonth/${month + 1}/$year"
                selectedDate = date
                birthdayText.text = "🎂 Birthday: $date"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            selectedImageUri = uri.toString()
            previewImage.setImageURI(uri)
        }
    }

    private fun saveData() {
        if (selectedDate.isNotEmpty() && selectedImageUri.isNotEmpty()) {
            val prefs = getSharedPreferences("BirthdayPrefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("birthday", selectedDate)
                putString("imageUri", selectedImageUri)
                putBoolean("hasData", true)
                apply()
            }
            Toast.makeText(this, "Birthday info saved!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please select both date and image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedData() {
        val prefs = getSharedPreferences("BirthdayPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("hasData", false)) {
            val date = prefs.getString("birthday", "") ?: ""
            val uri = prefs.getString("imageUri", "") ?: ""
            selectedDate = date
            selectedImageUri = uri
            birthdayText.text = "🎂 Birthday: $date"
            previewImage.setImageURI(Uri.parse(uri))
        }
    }

    private fun clearData() {
        val prefs = getSharedPreferences("BirthdayPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        birthdayText.text = "🎂 Birthday: Not Set"
        previewImage.setImageDrawable(null)
        selectedDate = ""
        selectedImageUri = ""
        Toast.makeText(this, "Data cleared", Toast.LENGTH_SHORT).show()
    }
}