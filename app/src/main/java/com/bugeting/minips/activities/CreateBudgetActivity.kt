package com.bugeting.minips.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val saveBudgetBtn: Button = findViewById(R.id.save_budgetBtn);
        val budgetNameET: EditText = findViewById(R.id.category_name_ET)
        val budgetAmountET: EditText = findViewById(R.id.category_amount_ET)

        val intentMainPage = Intent(this, MainPage::class.java)

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