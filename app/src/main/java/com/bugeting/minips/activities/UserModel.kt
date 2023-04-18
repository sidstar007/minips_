package com.bugeting.minips.activities

class UserModel(var userName: String, var userEmail: String, var userPass: String) {

    fun setName(name: String) {
        this.userName=name
    }
    fun getName(): String {
        return userName
    }


    fun setEmail(email: String) {
        this.userEmail=email
    }
    fun getEmail(): String {
        return userEmail
    }

    fun setPass(password: String) {
        this.userPass=password
    }
    fun getPass(): String {
        return userPass
    }
}