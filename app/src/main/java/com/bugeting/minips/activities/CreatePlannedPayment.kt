package com.bugeting.minips.activities

import android.app.*
import android.content.Context
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
import com.bugeting.minips.databinding.ActivityCreatePlannedPaymentBinding
import java.sql.Date
import java.util.*

class CreatePlannedPayment : AppCompatActivity() {

    private lateinit var binding : ActivityCreatePlannedPaymentBinding
    var catId: Int = 0

    companion object {
        const val CAT_ID="CAT_ID"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_planned_payment)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityCreatePlannedPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        catId=intent.getIntExtra(CAT_ID,0)

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
                    createNotificationChannel()
                    scheduleNotification()
                    Toast.makeText(this,"Payment added to planner successfully!",Toast.LENGTH_SHORT).show()

                }
                else {
                    Toast.makeText(this,"Failed to add payment to planner!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = binding.ppNameET.text.toString()
        val message = "\u20B9" + binding.ppAmountET.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nAmount: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->
                val intentTransactionPage = Intent(this,TransactionPage::class.java)
                intentTransactionPage.putExtra("CAT_ID",catId)
                startActivity(intentTransactionPage)
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getTime(): Long
    {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}