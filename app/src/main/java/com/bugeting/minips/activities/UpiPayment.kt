package com.bugeting.minips.activities


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bugeting.minips.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class UpiPayment : AppCompatActivity() {

    companion object {
        const val GOOGLE_PAY_REQUEST_CODE = 123
    }

    val databaseHelper = DatabaseHelper(this)
    var remarksText: String = ""

    var catId: Int = 0
    var payeeAmount: EditText? = null
    var payeeName: EditText? = null
    var payeeUPI: EditText? = null
    var remarks: EditText? = null
    var makePayment: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upi_payment)

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

        catId = intent.getIntExtra("CAT_ID", 0)

        payeeAmount = findViewById<EditText>(R.id.payeeAmountET)
        payeeName = findViewById<EditText>(R.id.payeeNameET)
        payeeUPI = findViewById<EditText>(R.id.payeeIdET)
        remarks= findViewById<EditText>(R.id.payeeRemarkET)
        makePayment= findViewById<Button>(R.id.makePaymentBtn)

        makePayment!!.setOnClickListener {
            if (payeeAmount!!.text.isEmpty() || payeeName!!.text.isEmpty() || payeeUPI!!.text.isEmpty() || remarks!!.text.isEmpty()) {
                Toast.makeText(this,"Please enter all the details!",Toast.LENGTH_SHORT).show()
            }
            else {
                val upi: String = payeeUPI!!.text.toString()
                val amount: String = payeeAmount!!.text.toString()
                val name: String = payeeName!!.text.toString()
                val remarks: String = remarks!!.text.toString()

                val GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"

                val uri = Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", upi)
                    .appendQueryParameter("pn", name)
                    .appendQueryParameter("mc", "")
                    .appendQueryParameter("tr", "your-transaction-ref-id")
                    .appendQueryParameter("tn", remarks)
                    .appendQueryParameter("am", amount)
                    .appendQueryParameter("cu", "INR")
                    .appendQueryParameter("url", "")
                    .build()

                //val uri = Uri.parse("upi://pay?pa=paytmqr2810050501011batnfp6464t@paytm&pn=Paytm%20Merchant&paytmqr=2810050501011BATNFP6464T")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri

                intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
                startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val response = data?.getStringExtra("response")
                    val status = response?.split("&")?.get(0)?.split("=")?.get(1)
                    if (status == "Success") {
                        val note = remarks?.text.toString()
                        val amount = payeeAmount?.text.toString()

                        val time = LocalTime.now()
                        val sTime=DateTimeFormatter.ofPattern("HH:mm", Locale.US).format(time)
                        val date = LocalDate.now()
                        val sDate=DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.UK).format(date)

                        val transactionId = data?.getStringExtra("response")

                        Toast.makeText(this,"Transaction Successful!",Toast.LENGTH_LONG).show()

                        databaseHelper.addTransaction(TransactionModel(0,catId,1,note,amount.toInt(),sDate,sTime))
                        databaseHelper.updateCategoryTransaction(TransactionModel(0,catId,1,note,amount.toInt(),sDate,sTime))

                        val intentTransPage = Intent(this,TransactionPage::class.java)
                        intentTransPage.putExtra("CAT_ID",catId)
                        startActivity(intentTransPage)
                    }

                    else {
                        Toast.makeText(this,"Transaction failed",Toast.LENGTH_LONG).show()
                    }

                }
                RESULT_CANCELED -> {
                    Toast.makeText(this,"Transaction cancelled",Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this,"Transaction failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
