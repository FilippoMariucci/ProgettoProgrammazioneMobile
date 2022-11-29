package com.example.progettoprogrammazionemobile

object RegistrationUtil {

    private val existingEmail = listOf("test1@gmail.com", "test2@gmail.com")
    /*
    ad esempio input non valido se
    email o password vuoti,
    password che non matchano
    password minore di 6 caratteri
     */
//per test


    fun validateRegistrationInput(

        email: String,
        password: String,
        confirmPassword: String
    ): Boolean{

        if(email.isEmpty() || password.isEmpty()){
            return false
        }
        if(email in existingEmail){
            return false
        }
        if(password != confirmPassword){
            return false
        }
        if(password.count{it.isDigit()} < 6) {
            return false
        }
        return true
    }
}