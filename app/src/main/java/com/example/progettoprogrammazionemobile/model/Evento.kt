package com.example.progettoprogrammazionemobile.model

import android.graphics.Bitmap

data class Evento (
    var id_evento: String ?= null,
    var titolo: String ?=null,
    var descrizione: String ?=null,
    var lingue: String ?=null,
    var categoria: String ?=null,
    var citta: String ?=null,
    var indirizzo: String ?=null,
    var data_evento: String ?=null,
    var costo: String ?= null,
    var n_persone: String ?= null,
    var foto: String?= null,
    var userId: String ?= null
    )
