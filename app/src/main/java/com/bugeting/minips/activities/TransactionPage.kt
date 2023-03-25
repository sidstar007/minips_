package com.bugeting.minips.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        categoryTitle.text=databaseHelper.viewCategoryName(catId)

        val transactionModelArrayList: ArrayList<TransactionModel> = databaseHelper.viewTransaction(catId)

        //Setting up transaction recycler view
        val transactionAdapter = TransactionAdapter(this, transactionModelArrayList)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        transRV.layoutManager = linearLayoutManager
        transRV.adapter = transactionAdapter

        cashInBtn.setOnClickListener {
            val intentCreateTransaction = Intent(this,CreateTransaction::class.java)
            intentCreateTransaction.putExtra("CAT_ID",catId)
            intentCreateTransaction.putExtra("TYPE",0)
            startActivity(intentCreateTransaction)
        }

        cashOutBtn.setOnClickListener {
            val intentCreateTransaction = Intent(this,CreateTransaction::class.java)
            intentCreateTransaction.putExtra("CAT_ID",catId)
            intentCreateTransaction.putExtra("TYPE",1)
            startActivity(intentCreateTransaction)
        }

    }

    override fun onBackPressed() {
        val intentMainPage = Intent(this, MainPage::class.java)
        startActivity(intentMainPage)
        finish()
    }
}