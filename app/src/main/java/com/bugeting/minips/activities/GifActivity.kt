package com.bugeting.minips.activities

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bugeting.minips.R

class GifActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)

        //Hiding the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val gifView = findViewById<ImageView>(R.id.gifView)
        val levelName = findViewById<TextView>(R.id.levelNameTV)

        val databaseHelper = DatabaseHelper(this)

        val userLevel = databaseHelper.viewUserLevel()

        if (userLevel==0) {
            gifView.setBackgroundResource(R.drawable.beginner)
            levelName.text = "Beginner"
        }
        else if (userLevel==1) {
            levelName.text = "Novice"
        }
        else if (userLevel==3) {
            levelName.text = "Brokie To Rockie"
        }
        else if (userLevel==4) {
            levelName.text = "Club Member"
        }
        else if (userLevel==5) {
            levelName.text = "Lone Wolf"
        }
        else if (userLevel==6) {
            levelName.text = "Leader"
        }
        else if (userLevel==7) {
            levelName.text = "Matrix Escaped"
        }
        else if (userLevel==8) {
            levelName.text = "Conquerer"
        }
        else if (userLevel==9) {
            gifView.setBackgroundResource(R.drawable.sigma)
            levelName.text = "Sigma"
        }
    }
}