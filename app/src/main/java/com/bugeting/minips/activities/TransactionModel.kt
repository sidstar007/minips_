package com.bugeting.minips.activities

import java.sql.Time
import java.util.*

class TransactionModel(var transId: Int,var catId: Int, var transType: Int,var transNote: String, var transAmount: Int, var transDate: String, var transTime: String) {
    //Getters and Setters
    @JvmName("getTransId1")
    fun getTransId(): Int {
        return transId
    }

    @JvmName("setTransId1")
    fun setTransId(id: Int) {
        this.transId=id
    }

    @JvmName("getTransAmount1")
    fun getTransAmount(): Int {
        return transAmount
    }

    @JvmName("setTransAmount1")
    fun setTransAmount(amount: Int) {
        this.transAmount=amount
    }

    @JvmName("getTransDate1")
    fun getTransDate(): String {
        return transDate
    }

    @JvmName("setTransDate1")
    fun setTransDate(date: String) {
        this.transDate=date
    }

    @JvmName("getTransNote1")
    fun getTransNote(): String {
        return transNote
    }

    @JvmName("setTransNote1")
    fun setTransNote(note: String) {
        this.transNote=note
    }

    @JvmName("getTransTime1")
    fun getTransTime(): String {
        return transTime
    }

    @JvmName("setTransTime1")
    fun setTransTime(transTime: String) {
        this.transTime=transTime
    }

    fun setTransCatId(catId: Int) {
        this.catId=catId
    }

    fun getTransCatId(): Int {
        return catId
    }

    @JvmName("setTransType1")
    fun setTransType(transType: Int) {
        this.transType=transType
    }

    @JvmName("getTransType1")
    fun getTransType(): Int {
        return transType
    }
}