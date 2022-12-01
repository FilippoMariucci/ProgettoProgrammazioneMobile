package com.example.progettoprogrammazionemobile.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//dati che mi servono per le immagini
@Entity(tableName = "image_table")

data class ImageUrlDb(
    @PrimaryKey @ColumnInfo(name = "url")
    var url: String,
    @ColumnInfo(name = "id_evento")
    var id_evento: String,
){
    constructor() : this("","",)
}