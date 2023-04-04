package com.bugeting.minips.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bugeting.minips.R
import com.google.android.material.textfield.TextInputEditText
import java.lang.Math.abs

class CategoryAdapter(private val context: Context,categoryModelArrayList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val categoryModelArrayList: ArrayList<CategoryModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val model: CategoryModel = categoryModelArrayList[position]
        holder.cardtitle.text=model.getCategoryName()
        if (model.getBalance()<0) {
            holder.cardbalance.text="-" + "\u20B9" + abs(model.getBalance()).toString()
        }
        else {
            holder.cardbalance.text = "\u20B9" + model.getBalance().toString()
        }


        //Deleting or Renaming category
        holder.catEditBtn.setOnClickListener {
            val editDialog = AlertDialog.Builder(holder.itemViewContext)

            editDialog.setTitle("Edit Category")
            editDialog.setPositiveButton("Rename") {_,_,->
                val editNameDialog = AlertDialog.Builder(holder.itemViewContext)
                val catNameChangeInput : TextInputEditText = TextInputEditText(holder.itemViewContext)

                editNameDialog.setTitle("Enter new name:")
                editNameDialog.setView(catNameChangeInput)

                //New Name Input
                val newName=catNameChangeInput.text

                //Save new name
                editNameDialog.setPositiveButton("Save") {_,_,->
                    model.setCategoryName(newName.toString())
                    holder.databaseHelper.updateCategory(model)
                    val intentMainPage = Intent(holder.itemViewContext,MainPage::class.java)
                    Toast.makeText(holder.itemViewContext,"Renamed Successfully.",Toast.LENGTH_LONG).show()

                    //Restarting Main Page to make changes visible
                    holder.itemViewContext.startActivity(intentMainPage)

                }
                //Cancel name change
                editNameDialog.setNegativeButton("Cancel") {_,_,->
                    //Cancelling name change
                }

                //Display Edit Name Dialog Box
                editNameDialog.show()
            }

            editDialog.setNegativeButton("Delete") {_,_,->
                //Check again before deleting category
                val checkAgain = AlertDialog.Builder(holder.itemViewContext)

                checkAgain.setTitle("Warning: Deletion CANNOT be undone.")
                checkAgain.setPositiveButton("Go Ahead") {_,_,->
                    //Deleting category
                    holder.databaseHelper.deleteAllTransactionOfCategory(model)
                    holder.databaseHelper.deleteCategory(model)
                    val intentMainPage = Intent(holder.itemViewContext,MainPage::class.java)

                    Toast.makeText(holder.itemViewContext,"Budget Deleted Successfully.",Toast.LENGTH_LONG).show()

                    //Restarting Main Page to make changes visible
                    holder.itemViewContext.startActivity(intentMainPage)
                }
                checkAgain.setNeutralButton("Cancel") { _, _, ->
                    //Cancel Deletion
                }

                //Displaying Check Again dialog box
                checkAgain.show()
            }

            editDialog.setNeutralButton("Cancel") {_,_,->
                //Cancel Edit Dialog Box
            }
            editDialog.show()
        }

        //Accessing Transaction Page of a category
        holder.innerCardLL.setOnClickListener {
            Toast.makeText(holder.itemViewContext,"card clicked",Toast.LENGTH_SHORT).show()
            val mp = MainPage()
            mp.finish()
            //Passing Category ID to transaction page and starting transaction page
            val intentTransactionPage = Intent(holder.itemViewContext,TransactionPage::class.java)
            intentTransactionPage.putExtra("CAT_ID",model.getId())
            holder.itemViewContext.startActivity(intentTransactionPage)
        }
        if (model.getBalance()<0) {
            holder.cardbalance.setTextColor(Color.parseColor("#FF0800"))
        }
    }

    override fun getItemCount(): Int {
        return categoryModelArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardtitle: TextView
        val cardbalance: TextView
        val catEditBtn: Button
        val databaseHelper: DatabaseHelper
        val dialog: Dialog
        val dialogChangeName: Dialog
        val itemViewContext: Context = itemView.context
        val innerCardLL: LinearLayout
        init {
            cardtitle = itemView.findViewById<TextView>(R.id.cardTitle)
            cardbalance = itemView.findViewById<TextView>(R.id.cardBalance)
            catEditBtn = itemView.findViewById<Button>(R.id.catEditBtn)
            databaseHelper = DatabaseHelper(itemView.context)
            dialog = Dialog(itemView.context);
            dialogChangeName = Dialog(itemView.context)
            innerCardLL = itemView.findViewById<LinearLayout>(R.id.innerCardLL)
        }
    }

    init {
        this.categoryModelArrayList = categoryModelArrayList
    }

}