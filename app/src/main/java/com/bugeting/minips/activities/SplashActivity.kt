package com.bugeting.minips.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bugeting.minips.R
import java.util.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Removing Action Bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#000000")
        }

        val databaseHelper = DatabaseHelper(this)

        //Checking if user has already logged in
        if (databaseHelper.userCount()==0) {
            val intentSignUp = Intent(this,SignUpActivity::class.java)

            //Displaying splash screen for 1.5 seconds
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    startActivity(intentSignUp)
                }
            }, 1500)
        }
        else {
            val intentMainPage = Intent(this, MainPage::class.java)

            //Displaying splash screen for 1.8 seconds
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    startActivity(intentMainPage)
                }
            }, 1800)
        }
    }
}