package com.bugeting.minips.activities

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteException
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.currentCoroutineContext
import java.sql.Date
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION=28
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

        //User Table
        private val TABLE_USER="userTable"
        private val USER_NAME="user_name"
        private val USER_EMAIL="user_email"
        private val USER_PASS="user_pass"
        private val USER_INCOME="user_income"
        private val USER_SAVING="user_saving"
        private val USER_LEVEL="user_level"

        //Planned Payments
        private val TABLE_PLAN="planTable"
        private val PLAN_ID="plan_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //Creating category table
        val query1 = ("CREATE TABLE " + TABLE_CAT + " (" + CAT_ID + " INTEGER PRIMARY KEY, " + CAT_NAME + " TEXT, " + CAT_BAL + " INTEGER"  + ")")
        db?.execSQL(query1)
        //Creating transaction table
        val query2 = ("CREATE TABLE " + TABLE_TRANS + " (" + TRANS_ID + " INTEGER PRIMARY KEY, " + CAT_ID + " INTEGER, " + TRANS_TYPE + " INTEGER, " + TRANS_NOTE + " TEXT, " + TRANS_AMOUNT + " INTEGER, " + TRANS_DATE + " TEXT, " + TRANS_TIME + " TEXT, " + "FOREIGN KEY ($CAT_ID) REFERENCES $TABLE_CAT($CAT_ID) ON DELETE CASCADE ON UPDATE CASCADE" + ")")
        db?.execSQL(query2)
        //Creating user table
        val query3 = ("CREATE TABLE " + TABLE_USER + " (" + USER_NAME + " TEXT, " + USER_EMAIL + " TEXT, " + USER_PASS + " TEXT, " + USER_SAVING + " INTEGER, " + USER_INCOME + " INTEGER, " + USER_LEVEL + " INTEGER DEFAULT 0" + ")")
        db?.execSQL(query3)
        val query4 = ("CREATE TABLE " + TABLE_PLAN + " (" + PLAN_ID + " INTEGER PRIMARY KEY, " + CAT_ID + " INTEGER, " + TRANS_TYPE + " INTEGER, " + TRANS_NOTE + " TEXT, " + TRANS_AMOUNT + " INTEGER, " + TRANS_DATE + " TEXT, " + TRANS_TIME + " TEXT, " + "FOREIGN KEY ($CAT_ID) REFERENCES $TABLE_CAT($CAT_ID) ON DELETE CASCADE ON UPDATE CASCADE" + ")")
        db?.execSQL(query4)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAN)
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

    //Function to add user
    fun addUser(user: UserModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(USER_NAME,user.userName)
        contentValues.put(USER_EMAIL,user.userEmail)
        contentValues.put(USER_PASS,user.userPass)

        val success = db.insert(TABLE_USER,null,contentValues)
        db.close()
        return success
    }

    //Function to get user count
    fun userCount(): Int {
        val db = this.writableDatabase
        var cursor: Cursor
        val query = "SELECT COUNT($USER_NAME) FROM $TABLE_USER"
        cursor = db.rawQuery(query,null)

        var count=0

        if (cursor.moveToFirst()) {
            count=cursor.getInt(0)
        }

        return count
        db.close()
        return count
    }

    //Function to get user name
    fun getUserName(): String {
        val db = this.writableDatabase
        var cursor: Cursor
        val query = "SELECT $USER_NAME FROM $TABLE_USER"
        cursor = db.rawQuery(query,null)

        var name = "Minips"

        if (cursor.moveToFirst()) {
            name=cursor.getString(0)
        }

        return name
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

    //Function to view category name
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

    //Function to get all the debited values
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

    //Function to get all the credited values
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

    //Function to delete transaction based on category id
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
    //Function to view all the transaction (stored in ArrayList)
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

    //Updating category table after deleting a specific transaction (update the balance)
    fun updateCategoryTransaction(trans: TransactionModel) {
        val db = this.writableDatabase

        var transAmount=trans.getTransAmount()
        if (trans.transType==1) {
            transAmount*=-1
        }

        val query="UPDATE $TABLE_CAT SET $CAT_BAL=$CAT_BAL+$transAmount WHERE $CAT_ID = ${trans.getTransCatId()}"
        db?.execSQL(query)
    }

    //Deleting all the transactions of a particular category
    fun deleteAllTransactionOfCategory(cat: CategoryModel) {
        val db=this.writableDatabase

        val query="DELETE FROM $TABLE_TRANS WHERE $CAT_ID=${cat.id}"
        db.execSQL(query)
        db.close()
    }

    //Function to get debited amount of a particular category
    fun getCatDebit(catId: Int): Int {
        val db=this.writableDatabase
        var cursor: Cursor? = null
        val query = "SELECT SUM($TRANS_AMOUNT) FROM $TABLE_TRANS WHERE $CAT_ID=${catId} AND $TRANS_TYPE=1"
        var catDebit = 0
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                catDebit = cursor.getInt(0)
            }
        }
        catch (e: SQLiteException) {
            return catDebit
        }
        db.close()
        return catDebit
    }

    fun getAllDebit(): Int {
        val db=this.writableDatabase
        var cursor: Cursor? = null
        val query = "SELECT SUM($TRANS_AMOUNT) FROM $TABLE_TRANS WHERE $TRANS_TYPE=1"
        var catDebit = 0
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                catDebit = cursor.getInt(0)
            }
        }
        catch (e: SQLiteException) {
            return catDebit
        }
        db.close()
        return catDebit
    }

    //Function to get debited amount of a particular category
    fun getCatCredit(catId: Int): Int {
        val db=this.writableDatabase
        var cursor: Cursor? = null
        val query = "SELECT SUM($TRANS_AMOUNT) FROM $TABLE_TRANS WHERE $CAT_ID=${catId} AND $TRANS_TYPE=0"
        var catDebit = 0
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                catDebit = cursor.getInt(0)
            }
        }
        catch (e: SQLiteException) {
            return catDebit
        }
        db.close()
        return catDebit
    }

    //Function to get category name and total debit
    @SuppressLint("Range")
    fun getDebitAndName(): ArrayList<PieEntry> {
        val pieList: ArrayList<PieEntry> = ArrayList<PieEntry>()

        val query = "SELECT SUM(y.$TRANS_AMOUNT) AS TRANS_AMOUNT, x.$CAT_NAME FROM $TABLE_CAT AS x INNER JOIN $TABLE_TRANS AS y ON x.$CAT_ID=y.$CAT_ID WHERE y.$TRANS_TYPE=1 GROUP BY x.$CAT_ID"
        var cursor: Cursor? = null
        val db = this.writableDatabase

        try {
            cursor = db.rawQuery(query,null)
            if (cursor.moveToFirst()) {
                do {
                    pieList.add(PieEntry(cursor.getFloat(cursor.getColumnIndex("TRANS_AMOUNT")), cursor.getString(cursor.getColumnIndex(
                        CAT_NAME))))
                } while (cursor.moveToNext())
            }
        }
        catch (e: SQLiteException) {
            db.close()
            return pieList
        }
        db.close()
        return pieList
    }

    fun addPlan(plan: TransactionModel): Long {
        val db=writableDatabase
        val contentValues = ContentValues()

        contentValues.put(TRANS_NOTE,plan.transNote)
        contentValues.put(TRANS_AMOUNT,plan.transAmount)
        contentValues.put(TRANS_TYPE,1);
        contentValues.put(TRANS_TIME,plan.transTime)
        contentValues.put(TRANS_DATE,plan.transDate)
        contentValues.put(CAT_ID,plan.catId)

        val success = db.insert(TABLE_PLAN,null,contentValues)
        db.close()
        return success
    }

    //Function to view plans of a particular category
    @SuppressLint("Range")
    fun viewPlan(catId: Int): ArrayList<TransactionModel> {
        val arrayListPlanModel: ArrayList<TransactionModel> = ArrayList()

        val selectQuery = "SELECT * FROM $TABLE_PLAN WHERE $CAT_ID = $catId"

        val db=this.writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery,null)
        }
        catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return arrayListPlanModel
        }

        var planID: Int
        var transAmount: Int
        var transNote: String
        var transDate: String
        var transTime: String
        var transType: Int

        if (cursor.moveToFirst()) {
            do {
                planID=cursor.getInt(cursor.getColumnIndex(PLAN_ID))
                transAmount=cursor.getInt(cursor.getColumnIndex(TRANS_AMOUNT))
                transDate=cursor.getString(cursor.getColumnIndex(TRANS_DATE))
                transTime=cursor.getString(cursor.getColumnIndex(TRANS_TIME))
                transNote=cursor.getString(cursor.getColumnIndex(TRANS_NOTE))
                transType=cursor.getInt(cursor.getColumnIndex(TRANS_TYPE))

                val plan = TransactionModel(planID,catId,transType,transNote,transAmount,transDate,transTime)
                arrayListPlanModel.add(plan)

            } while(cursor.moveToNext())
        }
        db.close()
        return arrayListPlanModel
    }

    fun addToTransaction(plan: TransactionModel): Long {
        val db=writableDatabase
        val contentValues = ContentValues()

        contentValues.put(TRANS_NOTE,plan.getTransNote())
        contentValues.put(TRANS_AMOUNT,plan.getTransAmount())
        contentValues.put(TRANS_TYPE,1);
        contentValues.put(CAT_ID,plan.catId)
        contentValues.put(TRANS_TIME,plan.getTransTime())
        contentValues.put(TRANS_DATE,plan.getTransDate())

        val success = db.insert(TABLE_TRANS,null,contentValues)
        deletePlan(plan)
        db.close()
        return success
    }

    fun deletePlan(plan: TransactionModel): Int {
        val db=this.writableDatabase

        val success = db.delete(TABLE_PLAN, PLAN_ID + "=" + plan.getTransId(),null)
        db.close()
        return success
    }

    fun deleteAllPlans(catId: Int): Int {
        val db=this.writableDatabase

        val success = db.delete(TABLE_PLAN, CAT_ID + "=" + catId,null)
        return success
    }

    fun viewUserName(): String {
        val db=this.writableDatabase
        val cursor: Cursor

        val selectQuery = "SELECT $USER_NAME FROM $TABLE_USER"
        cursor = db.rawQuery(selectQuery, null)

        var name: String = ""
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }

        db.close()
        return name
    }

    fun viewUserEmail(): String {
        val db = this.writableDatabase
        val cursor: Cursor

        val selectQuery = "SELECT $USER_EMAIL FROM $TABLE_USER"
        cursor = db.rawQuery(selectQuery, null)

        var email: String = ""
        if (cursor.moveToFirst()) {
            email = cursor.getString(0)
        }

        db.close()
        return email
    }

    fun viewUserIncome(): Int {
        val db=this.writableDatabase
        val cursor: Cursor

        val selectQuery = "SELECT $USER_INCOME FROM $TABLE_USER"
        cursor = db.rawQuery(selectQuery, null)

        var income: Int = 0
        if (cursor.moveToFirst()) {
            income = cursor.getInt(0)
        }

        db.close()
        return income
    }

    fun viewUserSaving(): Int {
        val db=this.writableDatabase
        val cursor: Cursor

        val selectQuery = "SELECT $USER_SAVING FROM $TABLE_USER"
        cursor = db.rawQuery(selectQuery, null)

        var save: Int = 0
        if (cursor.moveToFirst()) {
            save = cursor.getInt(0)
        }

        db.close()
        return save
    }

    fun viewUserLevel(): Int {
        val db=this.writableDatabase
        val cursor: Cursor

        val selectQuery = "SELECT $USER_LEVEL FROM $TABLE_USER"
        cursor = db.rawQuery(selectQuery, null)

        var save: Int = 0
        if (cursor.moveToFirst()) {
            save = cursor.getInt(0)
        }

        db.close()
        return save
    }

    fun updateIncome(income: Int) {
        val db = this.writableDatabase

        val query = "UPDATE $TABLE_USER SET $USER_INCOME=$income"
        db.execSQL(query)
    }

    fun updateSaving(saving: Int) {
        val db = this.writableDatabase

        val query = "UPDATE $TABLE_USER SET $USER_SAVING=$saving"
        db.execSQL(query)
    }

    fun updateLevel() {
        val db = this.writableDatabase

        val query = "UPDATE $TABLE_USER SET $USER_LEVEL=$USER_LEVEL+1"
        db.execSQL(query)
    }

    fun viewMaxID(): Int {
        val db = this.writableDatabase
        val cursor: Cursor

        val selectQuery = "SELECT MAX($TRANS_ID) FROM $TABLE_TRANS"
        cursor = db.rawQuery(selectQuery, null)

        var id: Int = 0
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }

        db.close()
        return id
    }

}
