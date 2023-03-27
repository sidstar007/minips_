package com.bugeting.minips.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugeting.minips.R

class TransactionPage : AppCompatActivity() {
    companion object {
        const val CAT_ID = "CAT_ID"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_page)

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = this.resources.getColor(R.color.black)
        }

        //Removing Action Bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        val databaseHelper = DatabaseHelper(this)

        //Retrieving category id from adapter class
        val catId = intent.getIntExtra(CAT_ID,0)

        val transRV = findViewById<RecyclerView>(R.id.transactionRV)
        val cashInBtn = findViewById<Button>(R.id.cashInBtn)
        val cashOutBtn = findViewById<Button>(R.id.cashOutBtn)
        val categoryTitle = findViewById<TextView>(R.id.transCategoryNameTV)

        //Retrieving category name
        categoryTitle.text=databaseHelper.viewCategoryName(catId)

        //ArrayList to store all transaction models
        val transactionModelArrayList: ArrayList<TransactionModel> = databaseHelper.viewTransaction(catId)

        //Setting up transaction recycler view
        val transactionAdapter = TransactionAdapter(this, transactionModelArrayList)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        transRV.layoutManager = linearLayoutManager
        transRV.adapter = transactionAdapter

        //Cash in button
        cashInBtn.setOnClickListener {
            val intentCreateTransaction = Intent(this,CreateTransaction::class.java)
            intentCreateTransaction.putExtra("CAT_ID",catId)
            intentCreateTransaction.putExtra("TYPE",0)
            startActivity(intentCreateTransaction)
        }

        //Cash out button
        cashOutBtn.setOnClickListener {
            val intentCreateTransaction = Intent(this,CreateTransaction::class.java)
            intentCreateTransaction.putExtra("CAT_ID",catId)
            intentCreateTransaction.putExtra("TYPE",1)
            startActivity(intentCreateTransaction)
        }

    }

    //Restart main page on back press
    override fun onBackPressed() {
        val intentMainPage = Intent(this, MainPage::class.java)
        startActivity(intentMainPage)
        finish()
    }
}