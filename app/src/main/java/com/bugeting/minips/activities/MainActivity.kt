package com.bugeting.minips.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.bugeting.minips.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sgnUp : Button = findViewById(R.id.sgnUpBtn)
        val sgnIn : Button = findViewById(R.id.sgnInBtn)
        val chkBtn : Button = findViewById(R.id.chooseRole)
        val createBudgetBtn : Button = findViewById(R.id.createBudgetBtn)
        val mainPageBtn : Button = findViewById(R.id.mainPage)
        val transPageBtn : Button = findViewById(R.id.transactionPage)

        val intentChooseType = Intent(this, ChooseType::class.java)
        val intentSignUp = Intent(this, SignUpActivity::class.java)
        val intentSignIn = Intent(this, SignInActivity::class.java)
        val intentCreateBudget = Intent(this, CreateBudgetActivity::class.java)
        val intentMainPage = Intent(this, MainPage::class.java)
        val intentTransactionPage = Intent(this, TransactionPage::class.java)

        chkBtn.setOnClickListener {
            Toast.makeText(this,"You are being navigated to choose type page.",Toast.LENGTH_SHORT).show()
            startActivity(intentChooseType)
        }

        sgnUp.setOnClickListener {
            Toast.makeText(this,"You are being navigated to sign up page.",Toast.LENGTH_SHORT).show()
            startActivity(intentSignUp)
        }

        sgnIn.setOnClickListener {
            Toast.makeText(this,"You are being navigated to sign in page.",Toast.LENGTH_SHORT).show()
            startActivity(intentSignIn)
        }

        createBudgetBtn.setOnClickListener {
            Toast.makeText(this,"You are being navigated to create budget page",Toast.LENGTH_SHORT).show()
            startActivity(intentCreateBudget)
        }

        mainPageBtn.setOnClickListener {
            Toast.makeText(this,"You are being navigated to main page",Toast.LENGTH_SHORT).show()
            startActivity(intentMainPage)
        }

        transPageBtn.setOnClickListener {
            Toast.makeText(this,"You are being navigated to transaction page",Toast.LENGTH_SHORT).show()
            startActivity(intentTransactionPage)
        }


    }
}