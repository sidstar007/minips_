package com.bugeting.minips.activities

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteException
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.currentCoroutineContext
import java.sql.Date
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION=12
        private val DATABASE_NAME="budget"

        //Category Table
        private val TABLE_CAT="category"
        private val CAT_ID="cat_id"
        private val CAT_NAME="cat_name"
        private val CAT_BAL="cat_bal"

        //Transaction Table
        private val TABLE_TRANS="transactionTable"
        private val TRANS_ID="trans_id"
        private val TRANS_NOTE="trans_note"
        private val TRANS_AMOUNT="trans_amount"
        private val TRANS_DATE="trans_date"
        private val TRANS_TIME="trans_time"
        private val TRANS_TYPE="trans_type"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        //Creating category table
        val query1 = ("CREATE TABLE " + TABLE_CAT + " (" + CAT_ID + " INTEGER PRIMARY KEY, " + CAT_NAME + " TEXT, " + CAT_BAL + " INTEGER" + ")")
        db?.execSQL(query1)
        //Creating transaction table
        val query2 = ("CREATE TABLE " + TABLE_TRANS + " (" + TRANS_ID + " INTEGER PRIMARY KEY, " + CAT_ID + " INTEGER, " + TRANS_TYPE + " INTEGER, " + TRANS_NOTE + " TEXT, " + TRANS_AMOUNT + " INTEGER, " + TRANS_DATE + " TEXT, " + TRANS_TIME + " TEXT, " + "FOREIGN KEY ($CAT_ID) REFERENCES $TABLE_CAT($CAT_ID) ON DELETE CASCADE ON UPDATE CASCADE" + ")")
        db?.execSQL(query2)
    }
    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANS)
        onCreate(db)
    }

    //Function to add category to category table
    fun addCategory(cat: CategoryModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CAT_NAME,cat.category_name)
        contentValues.put(CAT_BAL,cat.balance)

        val success = db.insert(TABLE_CAT,null,contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    //Function to view category table using array list
    fun viewCategory(): ArrayList<CategoryModel> {
        val arrayListCategoryModel: ArrayList<CategoryModel> = ArrayList<CategoryModel>()

        val selectQuery = "SELECT * FROM $TABLE_CAT"

        val db=this.writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery,null)
        }
        catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return arrayListCategoryModel
        }

        var id: Int
        var name: String
        var balance: Int

        if (cursor.moveToFirst()) {
            do {
                id=cursor.getInt(cursor.getColumnIndex(CAT_ID))
                name=cursor.getString(cursor.getColumnIndex(CAT_NAME))
                balance=cursor.getInt(cursor.getColumnIndex(CAT_BAL))

                val cat = CategoryModel(id,name,balance)
                arrayListCategoryModel.add(cat)
            } while (cursor.moveToNext())
        }
        db.close()
        return arrayListCategoryModel
    }

    //Function to update category table
    fun updateCategory(cat: CategoryModel): Int {
        val db=this.writableDatabase
        val contentValues=ContentValues()

        contentValues.put(CAT_NAME,cat.category_name)
        //contentValues.put(CAT_BAL,cat.balance)

        val success = db.update(TABLE_CAT, contentValues, CAT_ID + " = " + cat.id,null)
        db.close()
        return success
    }

    //Deleting category from category table
    fun deleteCategory(cat: CategoryModel): Int {
        val db=this.writableDatabase
        val contentValues=ContentValues()

        contentValues.put(CAT_ID,cat.id)

        val success=db.delete(TABLE_CAT,CAT_ID + " = " + cat.id,null)
        db.close()
        return success
    }

    fun viewCategoryName(catId: Int): String {
        val db = this.writableDatabase
        val selectQuery = "SELECT $CAT_NAME FROM $TABLE_CAT WHERE $CAT_ID=$catId"
        var cursor: Cursor? = null

        cursor=db.rawQuery(selectQuery,null)

        var name: String = ""
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        db.close()
        return name
    }

    //Getting sum of all the balances
    fun getSumBalance(): Long {
        val db=this.writableDatabase
        var cursor: Cursor? = null
        cursor=db.rawQuery("SELECT SUM($CAT_BAL) FROM $TABLE_CAT",null)

        var sum: Long = 0
        if (cursor.moveToFirst()) {
            sum = cursor.getLong(0)
        }
        db.close()
        return sum
    }

    //Function to get balance of a particular category
    fun getCatBalance(catId: Int): Int {
        val db=this.writableDatabase
        var cursor: Cursor? = null

        val query = "SELECT $CAT_BAL FROM $TABLE_CAT WHERE $CAT_ID=$catId"

        cursor=db.rawQuery(query,null)

        var balance=0
        if (cursor.moveToFirst()) {
            balance=cursor.getInt(0)
        }

        return balance
    }


    //Function to add transaction to transaction table
    fun addTransaction(trans: TransactionModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TRANS_NOTE,trans.transNote)
        contentValues.put(TRANS_AMOUNT,trans.transAmount)
        contentValues.put(TRANS_DATE,trans.transDate)
        contentValues.put(TRANS_TIME,trans.transTime)
        contentValues.put(CAT_ID,trans.catId)
        contentValues.put(TRANS_TYPE,trans.transType)

        val success = db.insert(TABLE_TRANS,null,contentValues)
        db.close()

        return success
    }

    fun getDebit(): Long {
        val db=this.writableDatabase
        var cursor: Cursor? = null
        cursor=db.rawQuery("SELECT SUM(CASE WHEN $TRANS_TYPE=1 THEN $TRANS_AMOUNT ELSE 0 END) FROM $TABLE_TRANS",null)

        var sum: Long=0
        if (cursor.moveToFirst()) {
            sum = cursor.getLong(0)
        }
        db.close()
        return sum
    }

    fun getCredit(): Long {
        val db=this.writableDatabase
        var cursor: Cursor? = null
        cursor=db.rawQuery("SELECT SUM(CASE WHEN $TRANS_TYPE=0 THEN $TRANS_AMOUNT ELSE 0 END) FROM $TABLE_TRANS",null)

        var sum: Long=0
        if (cursor.moveToFirst()) {
            sum = cursor.getLong(0)
        }
        db.close()
        return sum
    }

    fun deleteTransaction(trans: TransactionModel): Int {
        val db = this.writableDatabase
        trans.transAmount*=-1
        val contentValues = ContentValues()
        contentValues.put(TRANS_ID,trans.transId)

        this.updateCategoryTransaction(trans)

        val success = db.delete(TABLE_TRANS, TRANS_ID + " = " + trans.transId,null)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewTransaction(catId: Int): ArrayList<TransactionModel> {
        val arrayListTransactionModel: ArrayList<TransactionModel> = ArrayList()

        val selectQuery = "SELECT * FROM $TABLE_TRANS WHERE $CAT_ID = $catId"

        val db=this.writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery,null)
        }
        catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return arrayListTransactionModel
        }

        var transID: Int
        var transAmount: Int
        var transNote: String
        var transDate: String
        var transTime: String
        var transType: Int

        if (cursor.moveToFirst()) {
            do {
                transID=cursor.getInt(cursor.getColumnIndex(TRANS_ID))
                transAmount=cursor.getInt(cursor.getColumnIndex(TRANS_AMOUNT))
                transDate=cursor.getString(cursor.getColumnIndex(TRANS_DATE))
                transTime=cursor.getString(cursor.getColumnIndex(TRANS_TIME))
                transNote=cursor.getString(cursor.getColumnIndex(TRANS_NOTE))
                transType=cursor.getInt(cursor.getColumnIndex(TRANS_TYPE))

                val trans = TransactionModel(transID,catId,transType,transNote,transAmount,transDate,transTime)
                arrayListTransactionModel.add(trans)

            } while(cursor.moveToNext())
        }
        db.close()
        return arrayListTransactionModel
    }

    fun updateCategoryTransaction(trans: TransactionModel) {
        val db = this.writableDatabase

        var transAmount=trans.getTransAmount()
        if (trans.transType==1) {
            transAmount*=-1
        }

        val query="UPDATE $TABLE_CAT SET $CAT_BAL=$CAT_BAL+$transAmount WHERE $CAT_ID = ${trans.getTransCatId()}"
        db?.execSQL(query)
    }

    fun deleteAllTransactionOfCategory(cat: CategoryModel) {
        val db=this.writableDatabase

        val query="DELETE FROM $TABLE_TRANS WHERE $CAT_ID=${cat.id}"
        db.execSQL(query)
        db.close()
    }

}
