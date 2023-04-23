package com.bugeting.minips.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugeting.minips.R
import org.json.JSONObject

class CreateBudgetActivity : AppCompatActivity() {

    lateinit var suggestName: TextView
    var url = "https://api.openai.com/v1/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_budget)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        //Hiding the action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#5784DC")
        }

        //Getting all the buttons and text input fields
        val saveBudgetBtn: Button = findViewById(R.id.save_budgetBtn);
        val budgetNameET: EditText = findViewById(R.id.category_name_ET)
        val budgetAmountET: EditText = findViewById(R.id.category_amount_ET)
        suggestName = findViewById(R.id.suggestNameTV)
        val incomeBtn = findViewById<Button>(R.id.chooseIncome)
        val expenseBtn = findViewById<Button>(R.id.chooseExpense)

        incomeBtn.setOnClickListener {
            budgetAmountET.hint = "Enter Income"
        }
        expenseBtn.setOnClickListener {
            budgetAmountET.hint = "Budget Amount"
        }

        val query = "Suggest 3 to 4 unique budget category names"
        getResponse(query)

        //Main Page intent
        val intentMainPage = Intent(this, MainPage::class.java)

        //Saving a budget category
        saveBudgetBtn.setOnClickListener {
            if (budgetNameET.text.isEmpty() || budgetAmountET.text.isEmpty()) {
                Toast.makeText(this,"Please Enter All The Details.",Toast.LENGTH_SHORT).show()
            }
            else if (budgetAmountET.text.toString().toInt()<0) {
                Toast.makeText(this,"Please Enter a Valid Budget Amount.",Toast.LENGTH_SHORT).show()
            }
            else {
                val databaseHelper: DatabaseHelper = DatabaseHelper((this))
                val status=databaseHelper.addCategory(CategoryModel(0,budgetNameET.text.toString(),budgetAmountET.text.toString().toInt()))
                if (status>-1) {
                    Toast.makeText(this, "Category Created!", Toast.LENGTH_SHORT).show()
                    startActivity(intentMainPage)
                    finish()
                }
                else {
                    Toast.makeText(this,"Failed to Add Category.",Toast.LENGTH_SHORT).show()
                }
            }
        }
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

            object : JsonObjectRequest(
                Method.POST, url, jsonObject,
                Response.Listener { response ->

                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                    suggestName.text = "Some budget examples- " + responseMsg
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