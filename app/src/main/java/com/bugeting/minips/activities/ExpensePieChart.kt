package com.bugeting.minips.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.bugeting.minips.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ExpensePieChart : AppCompatActivity() {
    lateinit var pieChart: PieChart
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_pie_chart)

        //Removing Action Bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#337FF1")
        }

        pieChart=findViewById(R.id.pie_chart)

        val databaseHelper = DatabaseHelper((this))

        //Array list to store all the category debits
        val list:ArrayList<PieEntry> = databaseHelper.getDebitAndName()

        val pieDataSet = PieDataSet(list,"List")

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)

        //Setting pie chart value attributes
        pieDataSet.valueTextSize=15f
        pieDataSet.valueTypeface = Typeface.create(resources.getFont(R.font.inter_medium),Typeface.BOLD)
        pieDataSet.valueTextColor=Color.WHITE

        //Setting pie chart entry attributes
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(15f)
        pieChart.setEntryLabelTypeface(Typeface.create(resources.getFont(R.font.inter_medium),Typeface.BOLD))

        val pieData = PieData(pieDataSet)

        pieChart.data=pieData

        pieChart.setHoleColor(Color.BLACK)

        //Setting pie chart attributes
        pieChart.description.text = "Expense Analysis"
        pieChart.centerText="EXPENSES"
        pieChart.setCenterTextTypeface(Typeface.create(resources.getFont(R.font.inter_medium),Typeface.BOLD))
        pieChart.legend.textColor = Color.WHITE
        pieChart.setCenterTextColor(resources.getColor(R.color.cardBG))
        pieChart.setCenterTextSize(30f)
        pieChart.animateY(2000)
        pieChart.minAngleForSlices=25f

    }

    //Going to main page on back press
    override fun onBackPressed() {
        val intentMainPage = Intent(this,MainPage::class.java)
        finish()
        startActivity(intentMainPage)
    }
}