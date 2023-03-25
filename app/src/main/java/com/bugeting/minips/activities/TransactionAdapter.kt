package com.bugeting.minips.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bugeting.minips.R

class TransactionAdapter(private val context: Context, transactionModelArrayList: ArrayList<TransactionModel>):
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private val transactionModelArrayList: ArrayList<TransactionModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_transaction_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionAdapter.ViewHolder, position: Int) {
        val model: TransactionModel = transactionModelArrayList[position]
        holder.transDate.text=model.getTransDate()
        holder.transTime.text=model.getTransTime()
        holder.transRemark.text=model.getTransNote()

        //type->0 --> Credit
        if (model.getTransType()==0) {
            holder.transAmount.text ="+" + "\u20B9" + model.getTransAmount().toString()
            holder.transAmount.setTextColor(Color.parseColor("#00FF00"))
        }
        //type->1 --> Debit
        else if (model.getTransType()==1) {
            holder.transAmount.text = "-" + "\u20B9" + model.getTransAmount().toString()
            holder.transAmount.setTextColor(Color.parseColor("#FF0800"))
        }

        //Deleting transaction
        holder.transactionLL.setOnClickListener {
            val dialog = AlertDialog.Builder(holder.itemViewContext)

            dialog.setTitle("Delete Transaction")

            dialog.setPositiveButton("Delete") {_,_,->
                val dialog1 = AlertDialog.Builder(holder.itemViewContext)

                dialog1.setTitle("Are you sure you want to delete this transaction?")
                dialog1.setPositiveButton("Yes") {_,_,->
                    //Deleting transaction
                    holder.databaseHelper.deleteTransaction(model)
                    Toast.makeText(holder.itemViewContext,"Deleted Transaction.",Toast.LENGTH_LONG).show()
                    val intentTransactionPage = Intent(holder.itemViewContext,TransactionPage::class.java)
                    intentTransactionPage.putExtra("CAT_ID",model.catId)
                    holder.itemViewContext.startActivity(intentTransactionPage)
                }
                dialog1.setNegativeButton("Cancel") {_,_->
                //Cancel Deleting Transaction
                }
                dialog1.show()
            }

            dialog.setNegativeButton("Cancel") {_,_,->
            //Cancel Deleting Transaction
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return transactionModelArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transAmount: TextView
        val transDate: TextView
        val transRemark: TextView
        val transTime: TextView
        val itemViewContext: Context = itemView.context
        val databaseHelper: DatabaseHelper
        val transactionLL: LinearLayout

        init {
            transAmount=itemView.findViewById(R.id.transactionAmountTV)
            transDate=itemView.findViewById(R.id.transactionDateTV)
            transRemark=itemView.findViewById(R.id.transactionNoteTV)
            transTime=itemView.findViewById(R.id.transactionTimeTV)
            databaseHelper=DatabaseHelper(itemViewContext)
            transactionLL=itemView.findViewById(R.id.transactionLL)
        }
    }

    init {
        this.transactionModelArrayList = transactionModelArrayList
    }
}

