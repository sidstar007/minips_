package com.bugeting.minips.activities

class CategoryModel(var id: Int,var category_name: String, var balance: Int) {
    //Getters and Setter
    fun getCategoryName(): String {
        return category_name
    }

    fun setCategoryName(category_name: String) {
        this.category_name=category_name
    }

    @JvmName("getBalance1")
    fun getBalance() : Int {
        return balance
    }

    @JvmName("setBalance1")
    fun setBalance(balance: Int) {
        this.balance=balance
    }

    @JvmName("getId1")
    fun getId(): Int {
        return id
    }

    @JvmName("setId1")
    fun setId(id: Int) {
        this.id=id
    }
}