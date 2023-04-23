package com.bugeting.minips.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bugeting.minips.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PlanAdapter(private val context: Context, private var planModelArrayList: ArrayList<TransactionModel>):
    RecyclerView.Adapter<PlanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_planned_payments, parent, false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = planModelArrayList[position]

        holder.planName.text = model.getTransNote()
        holder.planAmount.text = "\u20B9" + model.getTransAmount().toString()
        holder.planDate.text = "Due: " + model.getTransDate()

        //Making a transaction from planned transaction
        holder.doPayment.setOnClickListener {
            holder.databaseHelper.deletePlan(model)
            val transName = model.transNote
            val transAmount = model.transAmount

            val time = LocalTime.now()
            val sTime= DateTimeFormatter.ofPattern("HH:mm", Locale.US).format(time)
            val date = LocalDate.now()
            val sDate= DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.UK).format(date)

            val status=holder.databaseHelper.addTransaction(TransactionModel(0,model.catId,1,transName,transAmount,sDate,sTime))
            holder.databaseHelper.updateCategoryTransaction(model)
            if (status>-1) {
                Toast.makeText(holder.itemView.context,"Transaction saved successfully!",Toast.LENGTH_SHORT).show()
                val intentTransactionPage = Intent(holder.itemView.context,TransactionPage::class.java)
                intentTransactionPage.putExtra("CAT_ID",model.catId)
                holder.itemView.context.startActivity(intentTransactionPage)
            }
            else {
                Toast.makeText(holder.itemView.context,"Transaction failed!",Toast.LENGTH_SHORT).show()
            }
        }

        //Cancelling a planned transaction
        holder.cancelPayment.setOnClickListener {
            val dialog = AlertDialog.Builder(holder.itemView.context)
            dialog.setTitle("Are you sure?")
            dialog.setPositiveButton("Yes") { _, _, ->
                holder.databaseHelper.deletePlan(model)
                Toast.makeText(holder.itemView.context,"Planned Payment Deleted Successfully.",Toast.LENGTH_SHORT).show()
                val intentTransactionPage =
                    Intent(holder.itemView.context, TransactionPage::class.java)
                intentTransactionPage.putExtra("CAT_ID", model.catId)
                holder.itemView.context.startActivity(intentTransactionPage)
            }
            dialog.setNeutralButton("Cancel") { _, _, -> }
            dialog.show()
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return planModelArrayList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val planName: TextView
        val planAmount: TextView
        val planDate: TextView
        val doPayment: Button
        val cancelPayment: Button
        val databaseHelper: DatabaseHelper

        init {
            planName = itemView.findViewById<TextView>(R.id.ppName)
            planAmount = itemView.findViewById<TextView>(R.id.ppAmount)
            planDate = itemView.findViewById<TextView>(R.id.ppDate)
            doPayment = itemView.findViewById<Button>(R.id.doPaymentBtn)
            cancelPayment = itemView.findViewById<Button>(R.id.cancelPaymentBtn)
            databaseHelper = DatabaseHelper(itemView.context)
        }
    }
    init {
        this.planModelArrayList = planModelArrayList
    }
}



