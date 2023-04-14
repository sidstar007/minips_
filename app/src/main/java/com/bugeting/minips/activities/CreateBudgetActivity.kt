package com.bugeting.minips.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import com.bugeting.minips.R

class CreateBudgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_budget)

        //Hiding the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#0012FF")
        }

        //Getting all the buttons and text input fields
        val saveBudgetBtn: Button = findViewById(R.id.save_budgetBtn);
        val budgetNameET: EditText = findViewById(R.id.category_name_ET)
        val budgetAmountET: EditText = findViewById(R.id.category_amount_ET)

        //Main Page intent
        val intentMainPage = Intent(this, MainPage::class.java)

        //Saving a budget category
        saveBudgetBtn.setOnClickListener {
            if (budgetNameET.text.isEmpty() || budgetAmountET.text.isEmpty()) {
                Toast.makeText(this,"Please Enter All The Details.",Toast.LENGTH_SHORT).show()
            }
            else if (budgetAmountET.text.toString().toInt()<0) {
                Toast.makeText(this,"Please Enter a Valid Budget Amount.",Toast.LENGTH_SHORT).show()
            }
            else {
                val databaseHelper: DatabaseHelper = DatabaseHelper((this))
                val status=databaseHelper.addCategory(CategoryModel(0,budgetNameET.text.toString(),budgetAmountET.text.toString().toInt()))
                if (status>-1) {
                    Toast.makeText(this, "Category Created!", Toast.LENGTH_SHORT).show()
                    startActivity(intentMainPage)
                    finish()
                }
                else {
                    Toast.makeText(this,"Failed to Add Category.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}