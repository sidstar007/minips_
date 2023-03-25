package com.bugeting.minips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.bugeting.minips.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val sgnBtn : Button = findViewById(R.id.sgnUpBtn)

        //Code to hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        sgnBtn.setOnClickListener {
            Toast.makeText(this,"Sign Up Button Clicked!",Toast.LENGTH_SHORT).show()
        }
    }
}