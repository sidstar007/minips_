package com.bugeting.minips.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.bugeting.minips.R
import java.util.*
import kotlin.math.min

class CreatePlannedPayment : AppCompatActivity() {

    companion object {
        const val CAT_ID="CAT_ID"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_planned_payment)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Hiding the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#0AA1DD")
        }

        val catId=intent.getIntExtra(CAT_ID,0)

        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val ppNameET = findViewById<EditText>(R.id.ppNameET)
        val ppAmountET = findViewById<EditText>(R.id.ppAmountET)
        val savePlan = findViewById<Button>(R.id.savePlan)

        val databaseHelper = DatabaseHelper(this)

        savePlan.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month+1
            val year = datePicker.year

            val hour = timePicker.hour
            val minute = timePicker.minute

            var time: String
            val date = day.toString() + "-" + month.toString() + "-" + year.toString()

            time = if ((minute/10).toInt()==0) {
                hour.toString() + ":" + "0" + minute.toString()
            } else {
                hour.toString() + ":" + minute.toString()
            }
            if (ppNameET.text.isEmpty() || ppAmountET.text.isEmpty()) {
                Toast.makeText(this,"Please enter all the details",Toast.LENGTH_SHORT).show()
            }
            else if (ppAmountET.text.toString().length>9) {
                Toast.makeText(this,"Exceeding max value for payment.",Toast.LENGTH_SHORT).show()
            }
            else {
                val status=databaseHelper.addPlan(TransactionModel(0,catId,1,ppNameET.text.toString(),ppAmountET.text.toString().toInt(),date,time))
                if (status>-1) {
                    val intentTransactionPage = Intent(this,TransactionPage::class.java)
                    Toast.makeText(this,"Payment added to planner successfully!",Toast.LENGTH_SHORT).show()
                    intentTransactionPage.putExtra("CAT_ID",catId)
                    startActivity(intentTransactionPage)
                }
                else {
                    Toast.makeText(this,"Failed to add payment to planner!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}