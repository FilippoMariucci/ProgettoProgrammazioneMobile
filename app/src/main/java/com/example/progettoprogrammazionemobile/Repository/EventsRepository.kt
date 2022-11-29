package com.example.progettoprogrammazionemobile.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.FirebaseDatabase.EventsDataFirebase
import com.example.progettoprogrammazionemobile.model.Partecipazione

class EventsRepository(private val database: EventsRoomDb) {

    // to avoid mismatch problem
    var eventsData = EventsDataFirebase(database)
    var events: LiveData<List<EventoDb>> = database.eventoDao().getAllEvents()
    lateinit var evento_to_delete : EventoDb
    //var filtered: LiveData<List<EventoDb>> = database.eventoDao().filterCategory("Adventure")

    fun getDataFromRemote() {
       database.clearAllTables()
//        eventsData.getAllEvents()
        var list = eventsData.boh()
        var prova = ArrayList<EventoDb>()
        if(list) {prova = eventsData.getList()}
        for (evento in prova){
//            val url_storage = "gs://programmazionemobile-a1b11.appspot.com/Users/${evento.id_evento}"
//            evento.foto = url_storage
            Log.d("giacomo", "$evento")
            database.eventoDao().insert(evento)
        }
    }

    fun filterCat(titleCat: String) : List<EventoDb>{
        val filtered = database.eventoDao().filterCategory(titleCat)
        return filtered
    }

    fun insert(model: EventoDb, imageUri: Uri) {
        /*val url_storage = "gs://programmazionemobile-a1b11.appspot.com/Users/${model.id_evento}"
        model.foto = url_storage*/
        database.eventoDao().insert(model)
        eventsData.inserEventRemote(model, imageUri)
    }

    fun delete(idEvento: String) {
        evento_to_delete = database.eventoDao().getEventoFromId(idEvento)
        database.eventoDao().deleteFromId(idEvento)
        database.imageDao().deleteImageFromIdEvento(idEvento)
        eventsData.deleteFromRemote(evento_to_delete)
    }

    fun getUserEvent(uid: String): List<EventoDb> {
        val list = database.eventoDao().userEvents(uid)
        return list
    }

    fun eventoToUpdate(idEvento: String): EventoDb {
        return database.eventoDao().getEventoFromId(idEvento)
    }

    /* CALL DAO TO UPDATE FIELDS */
    fun updateTitle(titolo: String, idEvento: String) {
        database.eventoDao().updateTitle(titolo, idEvento)
    }
    fun updateCategory(categoria: String, idEvento: String) {
        database.eventoDao().updateCategory(categoria, idEvento)
    }
    fun updateCitta(citta: String, idEvento: String) {
        database.eventoDao().updateCitta(citta, idEvento)
    }
    fun updateCosto(costo: String, idEvento: String) {
        database.eventoDao().updateCosto(costo, idEvento)
    }
    fun updateData(data: String, idEvento: String) {
        database.eventoDao().updateData(data, idEvento)
    }
    fun updateDescrizione(descrizione: String, idEvento: String) {
        database.eventoDao().updateDescrizione(descrizione, idEvento)
    }
    fun updateIndirizzo(indirizzo: String, idEvento: String) {
        database.eventoDao().updateIndirizzo(indirizzo, idEvento)
    }
    fun updateLingue(lingua: String, idEvento: String) {
        database.eventoDao().updateLingue(lingua, idEvento)
    }
    fun updateNPersone(npersone: String, idEvento: String) {
        database.eventoDao().updateNPersone(npersone, idEvento)
    }

    fun updateEventRemote(event: Map<String, String>, idEvento: String) {
        eventsData.updateEventOnRemote(event, idEvento)
    }

    fun addPartecipazione(idEvento: String, partecipazione: Partecipazione) {
        eventsData.addPartecipazioneRemote(idEvento, partecipazione)
    }
}