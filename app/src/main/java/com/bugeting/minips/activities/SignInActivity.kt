package com.bugeting.minips.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bugeting.minips.R

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }
}