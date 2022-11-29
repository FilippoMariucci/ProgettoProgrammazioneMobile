package com.example.progettoprogrammazionemobile.model

class User {
    var email: String? = null
    var name: String? = null
    var surname: String? = null
    var password: String? = null
    var birth: String? = null
    var state: String? = null
    var description: String? = null

    constructor(  name:String,  surname:String,
         password:String,  birth:String,  state:String, description:String){
        this.name = name
        this.surname = surname
        this.password = password
        this.birth = birth
        this.state = state
        this.description = description
    }
    constructor()
}

