package com.bugeting.minips.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugeting.minips.R
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class AccountInfo : AppCompatActivity() {

    lateinit var financialAdviceTV: TextView
    var url = "https://api.openai.com/v1/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //Changing color of status bar
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#19173D")
        }

        val userName = findViewById<TextView>(R.id.userNameTV)
        val userEmail = findViewById<TextView>(R.id.userEmailTV)
        val userIncome = findViewById<TextView>(R.id.userIncomeTV)
        val userSaving = findViewById<TextView>(R.id.userSavingTV)
        val editSaving = findViewById<Button>(R.id.editSaving)
        val editIncome = findViewById<Button>(R.id.editIncome)
        val userLevel = findViewById<TextView>(R.id.userRankTV)
        financialAdviceTV = findViewById(R.id.financialAdviceTV)

        val databaseHelper = DatabaseHelper(this)

        financialAdviceTV.movementMethod = ScrollingMovementMethod()

        val query = "Give some financial advice and investment advice in 2 to 3 lines for a person in India with income of rupees " + databaseHelper.viewUserIncome().toString() + " and savings goal of rupees " + databaseHelper.viewUserSaving().toString() + "."
        getResponse(query)



        userName.text = databaseHelper.viewUserName()
        userEmail.text = databaseHelper.viewUserEmail()
        userIncome.text = "\u20B9" + databaseHelper.viewUserIncome().toString()
        userSaving.text = "\u20B9" + databaseHelper.viewUserSaving().toString()
        userLevel.text = "Beginner"

        userLevel.setOnClickListener {
            val intentGifActivity = Intent(this,GifActivity::class.java)
            startActivity(intentGifActivity)
        }

        editSaving.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val editText = TextInputEditText(this)
            editText.inputType = InputType.TYPE_CLASS_NUMBER

            dialog.setTitle("Edit Savings Goal")
            dialog.setView(editText)

            dialog.setPositiveButton("Save") { _, _ ->
                if (editText.text!!.isEmpty() || editText.text.toString().toInt()<=0) {
                    Toast.makeText(this,"Please enter a valid amount!",Toast.LENGTH_SHORT).show()
                }
                else {
                    databaseHelper.updateSaving(editText.text.toString().toInt())
                    Toast.makeText(this,"Savings goal updated successfully!",Toast.LENGTH_SHORT).show()
                    recreate()
                }
            }
            dialog.setNeutralButton("Cancel") {_,_,->

            }
            dialog.show()
        }

        editIncome.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val editText = TextInputEditText(this)
            editText.inputType = InputType.TYPE_CLASS_NUMBER

            dialog.setTitle("Edit Income")
            dialog.setView(editText)

            dialog.setPositiveButton("Save") { _, _ ->
                if (editText.text!!.isEmpty() || editText.text.toString().toInt()<=0) {
                    Toast.makeText(this,"Please enter a valid amount!",Toast.LENGTH_SHORT).show()
                }
                else {
                    databaseHelper.updateIncome(editText.text.toString().toInt())
                    Toast.makeText(this,"Income updated successfully!",Toast.LENGTH_SHORT).show()
                    recreate()
                }
            }
            dialog.setNeutralButton("Cancel") {_,_,->

            }
            dialog.show()
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
                    financialAdviceTV.text = "Heres some personalised financial advice for you: " + responseMsg
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