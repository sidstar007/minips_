package com.bugeting.minips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.bugeting.minips.R

class ChooseType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_type)

        val stBtn : Button = findViewById(R.id.studentBtn);
        val proBtn : Button = findViewById(R.id.professionalBtn);

        stBtn.setOnClickListener {
            Toast.makeText(this, "You are a student!", Toast.LENGTH_SHORT).show()
        }
        proBtn.setOnClickListener {
            Toast.makeText(this,"You are a professional",Toast.LENGTH_SHORT).show()
        }
    }
}