package com.bugeting.minips.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bugeting.minips.R

/* Sign up activity is added just for placeholder purpose, shall be implemented properly in the future */

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val sgnBtn : Button = findViewById(R.id.sgnUpBtn)

        //Database Helper
        val databaseHelper = DatabaseHelper(this)

        //Getting user fields
        val emailET = findViewById<EditText>(R.id.emailEt)
        val passET = findViewById<EditText>(R.id.passEt)
        val nameET = findViewById<EditText>(R.id.nameEt)

        //Code to hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        sgnBtn.setOnClickListener {
            //Checking if any field is empty
            if (emailET.text.isEmpty() || passET.text.isEmpty() || nameET.text.isEmpty()) {
                Toast.makeText(this,"Please enter all the details",Toast.LENGTH_SHORT).show()
            }
            //Adding user credentials to database
            databaseHelper.addUser(UserModel(nameET.text.toString(),emailET.text.toString(),passET.text.toString()))
            Toast.makeText(this,"Signed in successfully",Toast.LENGTH_SHORT).show()

            //Starting main page
            val intentMainPage = Intent(this,MainPage::class.java)
            startActivity(intentMainPage)
            finish()
        }
    }
}