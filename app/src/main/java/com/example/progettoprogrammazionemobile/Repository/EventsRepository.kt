package com.example.progettoprogrammazionemobile.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.FirebaseDatabase.EventsDataFirebase
import com.example.progettoprogrammazionemobile.model.Partecipazione

class EventsRepository(private val database: EventsRoomDb) {


    var eventsData = EventsDataFirebase(database) //qui richiamo Events data firebase con database passato
    var events: LiveData<List<EventoDb>> = database.eventoDao().getAllEvents()  //la uso per leggere perche è live data e var
    lateinit var evento_to_delete : EventoDb  //tabella room degli eventi

    fun getDataFromRemote() {
       database.clearAllTables()  //pulisco tabelle del db room

        var list = eventsData.boh()  //funzione in Events data firebase che torna un flag booleano che uso in if sotto
        var prova = ArrayList<EventoDb>()
        if(list) {prova = eventsData.getList()} //su prova ci salvo eventi con info
        for (evento in prova){

            Log.d("okok", "$evento")
            database.eventoDao().insert(evento)
        }
    }

    fun filterCat(titleCat: String) : List<EventoDb>{
        val filtered = database.eventoDao().filterCategory(titleCat)
        return filtered
    }

    fun insert(model: EventoDb, imageUri: Uri) {

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



    //funzioni che richiamano funzioni del dao per fare aggiornamenti
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