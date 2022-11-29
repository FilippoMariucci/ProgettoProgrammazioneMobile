package com.example.appericolo.ui.preferiti.contacts.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evento_table")
data class EventoDb (
    @PrimaryKey @ColumnInfo(name = "id_evento")
    var id_evento :String,
    @ColumnInfo(name = "titolo")
    var titolo :String,
    @ColumnInfo(name = "descrizione")
    var descrizione :String,
    @ColumnInfo(name = "lingue")
    var lingue :String,
    @ColumnInfo(name = "categoria")
    var categoria :String,
    @ColumnInfo(name = "citta")
    var citta :String,
    @ColumnInfo(name = "indirizzo")
    var indirizzo :String,
    @ColumnInfo(name = "data_evento")
    var data_evento :String,
    @ColumnInfo(name = "costo")
    var costo :String,
    @ColumnInfo(name = "n_persone")
    var n_persone :String,
    @ColumnInfo(name = "foto")
    var foto :String,
    @ColumnInfo(name = "userId")
    var userId :String,
){
    constructor() : this("","null", "null","null" ,
        "null", "null","null","null","null","null","null","null")
}