package com.bugeting.minips.activities

/* Developed by: Siddhant Chalke
                 21BCS118
                 IIIT Dharwad

   UI/UX by:     Venkatesh Jaiswal
                 21BCS131
                 IIIT Dharwad
 */

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bugeting.minips.R
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Math.abs
import kotlin.system.exitProcess

class MainPage : AppCompatActivity() {

    //Back Press Time
    var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = this.resources.getColor(R.color.black)
        }

        //Database Helper
        val databaseHelper = DatabaseHelper((this))

        //Getting all the buttons and text views
        val addBudgetBtn = findViewById<FloatingActionButton>(R.id.addBudgetBtn)
        val categoryRV = findViewById<RecyclerView>(R.id.RVCategory)
        val balanceCardNum = findViewById<TextView>(R.id.cardMainBalanceNumTV)
        val expenseCardNum = findViewById<TextView>(R.id.cardMainExpenseNumTV)
        val incomeCardNum = findViewById<TextView>(R.id.cardMainIncomeNumTV)
        val expenseCardLL = findViewById<LinearLayout>(R.id.cardMainExpenseLL)

        //Intents
        val intentAddBudget = Intent(this, CreateBudgetActivity::class.java)
        val intentExpensePieChart = Intent(this,ExpensePieChart::class.java)

        //category model array list to store all the category models
        var categoryModelArrayList: ArrayList<CategoryModel> = ArrayList<CategoryModel>()

        //Floating Action Button to go to Add Budget Page
        addBudgetBtn.setOnClickListener {
            Toast.makeText(this,"add budget",Toast.LENGTH_SHORT).show()
            startActivity(intentAddBudget)
        }

        //Adapter and layout manager for the main page recycler view
        categoryModelArrayList = databaseHelper.viewCategory()
        val categoryAdapter = CategoryAdapter(this, categoryModelArrayList)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        categoryRV.layoutManager = linearLayoutManager
        categoryRV.adapter = categoryAdapter

        //Setting up main page expense card
        val expense = databaseHelper.getDebit()
        expenseCardNum.text="\u20B9" + expense.toString()

        //Setting up main page income page
        val income: Long = databaseHelper.getCredit()
        incomeCardNum.text="\u20B9" + income.toString()

        //Setting up main page balance card
        if (databaseHelper.getSumBalance()<0) {
            balanceCardNum.text="-" + "\u20B9" + abs(databaseHelper.getSumBalance()).toString()
            balanceCardNum.setTextColor(Color.parseColor("#FF0800"))
        }
        else {
            balanceCardNum.text = "\u20B9" + databaseHelper.getSumBalance().toString()
        }

        //Expense Pie Chart Activity
        expenseCardLL.setOnClickListener {
            Toast.makeText(this,"Expense card clicked!",Toast.LENGTH_SHORT).show()
            startActivity(intentExpensePieChart)
        }
    }
    /*override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
            exitProcess(0)
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }*/
}

/* To do:
    Fix Income logic
    Main Page negative budget balance --Fixed
    Delete transaction --Fixed
 */