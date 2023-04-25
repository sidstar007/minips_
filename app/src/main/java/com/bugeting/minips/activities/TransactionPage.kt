package com.bugeting.minips.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugeting.minips.R
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TransactionPage : AppCompatActivity() {
    companion object {
        const val CAT_ID = "CAT_ID"
    }

    lateinit var transactionModelArrayList: ArrayList<TransactionModel>
    lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_page)

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = parseColor("#0AA1DD")
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
        val filterTransaction = findViewById<Button>(R.id.filterTransaction)
        val addPlanPayment = findViewById<Button>(R.id.addPlanPayment)
        val ppRV = findViewById<RecyclerView>(R.id.plannedPaymentsRV)
        val sepLine1 = findViewById<View>(R.id.separatorLineTrans1)
        val sepLine2 = findViewById<View>(R.id.separatorLineTrans2)
        val catBalanceTV = findViewById<TextView>(R.id.transPageBalanceTV)
        val catCreditTV = findViewById<TextView>(R.id.transPageCreditTV)
        val catDebitTV = findViewById<TextView>(R.id.transPageDebitTV)
        val UPIbtn = findViewById<Button>(R.id.UPIPayBtn)

        UPIbtn.setOnClickListener {
            val intentUpiActivity = Intent(this,UpiPayment::class.java)
            intentUpiActivity.putExtra("CAT_ID",catId)
            startActivity(intentUpiActivity)
        }

        val cal = Calendar.getInstance()

        //Retrieving category name
        categoryTitle.text=databaseHelper.viewCategoryName(catId)

        //ArrayList to store all transaction models
        transactionModelArrayList = databaseHelper.viewTransaction(catId)
        val planModelArrayList: ArrayList<TransactionModel> = databaseHelper.viewPlan(catId)

        catBalanceTV.text = "\u20B9" + databaseHelper.getCatBalance(catId).toString()
        catCreditTV.text = "\u20B9" + (databaseHelper.getCatDebit(catId) + databaseHelper.getCatBalance(catId)).toString()
        catDebitTV.text = "\u20B9" + databaseHelper.getCatDebit(catId).toString()

        //Hiding separator line if plan array list is empty
        if (planModelArrayList.size==0) {
            sepLine1.visibility = View.GONE
            sepLine2.visibility = View.GONE
        }

        //Setting up transaction recycler view
        transactionAdapter = TransactionAdapter(this, transactionModelArrayList)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        transRV.layoutManager = linearLayoutManager
        transRV.adapter = transactionAdapter
        transactionAdapter.notifyDataSetChanged()

        //Setting up planner recycler view
        val planAdapter = PlanAdapter(this, planModelArrayList)
        val linearLayoutManagerPP = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ppRV.layoutManager = linearLayoutManagerPP
        ppRV.adapter = planAdapter
        planAdapter.notifyDataSetChanged()


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

        //Date picker
        val dateSetListener = (object : DatePickerDialog.OnDateSetListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                filter(sdf.format(cal.time))
            }
        })

        //Filter transaction based on date
        filterTransaction.setOnClickListener (object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@TransactionPage,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        //Add planned payment
        addPlanPayment.setOnClickListener {
            val intentCreatePlanPayment = Intent(this,CreatePlannedPayment::class.java)
            intentCreatePlanPayment.putExtra("CAT_ID",catId)
            startActivity(intentCreatePlanPayment)
        }
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<TransactionModel> = ArrayList()

        for (item in transactionModelArrayList) {

            if (item.transDate.contains(text)) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Transaction on this date Found", Toast.LENGTH_SHORT).show()
        } else {
            transactionAdapter.filterList(filteredlist)
        }
    }

    //Restart main page on back press
    override fun onBackPressed() {
        val intentMainPage = Intent(this, MainPage::class.java)
        startActivity(intentMainPage)
        finish()
    }
}