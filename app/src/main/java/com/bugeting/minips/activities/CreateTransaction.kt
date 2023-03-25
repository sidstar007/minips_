package com.bugeting.minips.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bugeting.minips.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

class CreateTransaction : AppCompatActivity() {

    companion object {
        const val CAT_ID= "CAT_ID"
        const val TYPE = "TYPE"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_transaction)

        val catId = intent.getIntExtra(CAT_ID,0)
        val type = intent.getIntExtra(TYPE,0)

        val intentTransactionPage = Intent(this,TransactionPage::class.java)

        val transTypeTV = findViewById<TextView>(R.id.transactionTypeTV)
        val saveTransactionBtn = findViewById<Button>(R.id.saveTransactionBtn)
        val transAmountET = findViewById<EditText>(R.id.transactionAmountET)
        val transRemarkET = findViewById<EditText>(R.id.transactionNoteET)

        if (type==0) {
            transTypeTV.text="Credit Amount"
        }
        else if (type==1) {
            transTypeTV.text="Debit Amount"
        }

        saveTransactionBtn.setOnClickListener {
            var transAmount: Int
            var transRemark: String = ""
            if (transAmountET.text.toString().isEmpty()) {
                Toast.makeText(this,"Please Enter the Amount.",Toast.LENGTH_SHORT).show()
            }
            else {
                if (transAmountET.text.toString().toInt()==0) {
                    Toast.makeText(this,"Please Enter Valid Amount!",Toast.LENGTH_SHORT).show()
                }
                else {
                    val databaseHelper = DatabaseHelper((this))

                    /*if (type==1 && transAmountET.text.toString().toInt()>databaseHelper.getCatBalance(catId)) {
                        val dialog = AlertDialog.Builder(this)

                        dialog.setTitle("You are exceeding your budget balance!")
                        dialog.setPositiveButton("Go Ahead") {_,_,->
                            TODO("Create a method for creating transaction")
                        }
                    }*/
                    transAmount = transAmountET.text.toString().toInt()
                    if (transRemarkET.text.isEmpty()) {
                        if (type==0) {
                            transRemark="Credited"
                        }
                        else if (type==1) {
                            transRemark="Debited"
                        }
                    }
                    else {
                        transRemark=transRemarkET.text.toString()
                    }

                    val time = LocalTime.now()
                    val sTime=DateTimeFormatter.ofPattern("HH:mm", Locale.US).format(time)
                    val date = LocalDate.now()
                    val sDate=DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.UK).format(date)

                    val status=databaseHelper.addTransaction(TransactionModel(0,catId,type,transRemark,transAmount,sDate,sTime))
                    databaseHelper.updateCategoryTransaction(TransactionModel(0,catId,type,transRemark,transAmount,sDate,sTime))
                    if (status>-1) {
                        Toast.makeText(this,"Transaction Added Successfully!",Toast.LENGTH_SHORT).show()
                        intentTransactionPage.putExtra("CAT_ID", catId)
                        startActivity(intentTransactionPage)
                        finish()
                    }
                    else {
                        Toast.makeText(this,"Transaction Failed.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}