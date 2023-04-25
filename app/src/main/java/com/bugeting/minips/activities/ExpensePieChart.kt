package com.bugeting.minips.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugeting.minips.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.json.JSONObject


class ExpensePieChart : AppCompatActivity() {
    lateinit var pieChart: PieChart
    lateinit var expenseAnalysisTV: TextView
    var url = "https://api.openai.com/v1/completions"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_pie_chart)

        // initializing variables on below line.
        expenseAnalysisTV = findViewById(R.id.expenseAnalysisTV)

        expenseAnalysisTV.movementMethod = ScrollingMovementMethod()

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

        var query="give some advice in 2 to 3 lines for these expenses in 80 words "

        val databaseHelper = DatabaseHelper((this))

        //Array list to store all the category debits
        val list:ArrayList<PieEntry> = databaseHelper.getDebitAndName()

        for (value in list) {
            query += " Rupees " + value.data.toString() + " for " + value.label.toString()
        }

        //Fetching response from Open AI API
        if (list.size>=1) {
            getResponse(query)
        }

        val pieDataSet = PieDataSet(list,"List")

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)

        //Setting pie chart value attributes
        pieDataSet.valueTextSize=15f
        pieDataSet.valueTypeface = Typeface.create(resources.getFont(R.font.inter_medium),Typeface.BOLD)
        pieDataSet.valueTextColor=Color.BLACK

        //Setting pie chart entry attributes
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(15f)
        pieChart.setEntryLabelTypeface(Typeface.create(resources.getFont(R.font.inter_medium),Typeface.BOLD))

        val pieData = PieData(pieDataSet)

        pieChart.data=pieData

        pieChart.setHoleColor(Color.BLACK)

        //Setting pie chart attributes
        pieChart.description.text = "Expense Analysis"
        pieChart.centerText="EXPENSES"
        pieChart.setCenterTextTypeface(Typeface.create(resources.getFont(R.font.inter_medium),Typeface.BOLD))
        pieChart.legend.textColor = Color.TRANSPARENT
        pieChart.setCenterTextColor(resources.getColor(R.color.head_color))
        pieChart.setCenterTextSize(30f)
        pieChart.animateY(2000)
        pieChart.minAngleForSlices=25f
        pieChart.setHoleColor(resources.getColor(R.color.bg_color))
        pieChart.legend.isEnabled = false

    }

    //Going to main page on back press
    override fun onBackPressed() {
        val intentMainPage = Intent(this,MainPage::class.java)
        finish()
        startActivity(intentMainPage)
    }

    private fun getResponse(query: String) {
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)

        val jsonObject: JSONObject? = JSONObject()

        jsonObject?.put("model", "text-davinci-003")
        jsonObject?.put("prompt", query)
        jsonObject?.put("temperature", 1)
        jsonObject?.put("max_tokens", 100)
        jsonObject?.put("top_p", 1)
        jsonObject?.put("frequency_penalty", 0.0)
        jsonObject?.put("presence_penalty", 0.0)

        val postRequest: JsonObjectRequest =

            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response ->

                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                        expenseAnalysisTV.text = "Your Expense Analysis: " + responseMsg
                },

                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json"
                    params["Authorization"] = "Bearer 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"
                    return params;
                }
            }
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })

        queue.add(postRequest)
    }
}