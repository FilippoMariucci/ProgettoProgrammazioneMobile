package com.example.progettoprogrammazionemobile.ViewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*


//import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
//import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.database.EventoDb
import com.example.progettoprogrammazionemobile.database.EventsRoomDb


import com.example.progettoprogrammazionemobile.Repository.EventsRepository
import com.example.progettoprogrammazionemobile.model.Partecipazione
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class eventViewModel(application: Application) : AndroidViewModel(application) {

    var readEventData: LiveData<List<EventoDb>> // è live data e var perchè viene letto e basta
    private val eventsRepository: EventsRepository
    lateinit var imageUri: Uri

    val filterEventsLiveData = MutableLiveData<List<EventoDb>>()  // val e mutable perche leggo e scrivo
    val userEvent = MutableLiveData<List<EventoDb>>()
    var eventoBeforeUpdate = EventoDb(  //parametri che passo a questo costruttore
        "",
        "null",
        "null",
        "null",
        "null",
        "null",
        "null",
        "null",
        "null",
        "null",
        "null",
        "null"
    )

    private var debouncePeriod: Long = 500  // quanto aspetto?? mi serve dopo
    private var searchJob: Job? = null  //particolare interfaccia per job in background

    init {
        eventsRepository = EventsRepository(EventsRoomDb.getDatabase(application))
        Log.d("events", "${eventsRepository.events}")
        readEventData = eventsRepository.events
        Log.d("pippo", "$readEventData")

    }

    //richiamo funzione dell'events repository per fare get di dati
    fun getDataFromRemote() {
        viewModelScope.launch(Dispatchers.IO){
            eventsRepository.getDataFromRemote()
        }
    }  //la uso dopo per altra funzione che la chiamerò refresh



     //qui uso interfaccia job
    fun onFilterQuery(titleCat: String) {
            searchJob?.cancel() //possibilità di annullare il job
            searchJob = viewModelScope.launch {
                delay(debouncePeriod)  //ritarda la coroutine per un certo tempo che decido io con parametro
                if (titleCat != "") {  // se  cerco qualcosa non  nullo allora va avanti
                    fetchEventByQuery(titleCat)
                }
            }
    }

    private fun fetchEventByQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filtered = eventsRepository.filterCat(query)  //prendo tutto dalla tabella eventi dove faccio ricerca su una certa categpria
            filterEventsLiveData.postValue(filtered)
        }
    }

    fun refreshFeed() {
        this.getDataFromRemote()  //funzione definita sopra
    }



    //funzione per salvare un evento
    fun saveEvento(model: EventoDb) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.insert(model, imageUri)
        }
    }
    fun setUri(imageUri: Uri) {
        this.imageUri = imageUri  // variabile definita all'inizio
    }
    fun getDateTimeCalendar(): ArrayList<Int> {
        val cal = Calendar.getInstance()
        var array = arrayListOf<Int>()  //per salvare info sulla data che poi uso per settare data e ora di un evento che creo
        var day = cal.get(Calendar.DAY_OF_MONTH)  //prendo tutto quello che mi interessa salvare
        var month = cal.get(Calendar.MONTH)
        var year = cal.get(Calendar.YEAR)
        var hour = cal.get(Calendar.HOUR)
        var minute = cal.get(Calendar.MINUTE)
        Log.d("datainserimento", "$day $month $year")
        array.add(day)  //inserisco nell'array queste info sulla data ,che sono tutti interi per come ho definito array
        array.add(month)
        array.add(year)
        array.add(hour)
        array.add(minute)
        return array
    }

    //per eliminare un certo evento
    fun deleteEvent(idEvento: String, uid:String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.delete(idEvento)
            val list = eventsRepository.getUserEvent(uid)
            userEvent.postValue(list)
        }
    }

    //prendo gli eventi di un certo user tramite il suo id
    fun getUserEvent(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = eventsRepository.getUserEvent(uid)
            userEvent.postValue(list)
        }
    }

    //per ritrovare il preciso  evento che voglio andare  a modificare
    fun eventoToUpdate(idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val event = eventsRepository.eventoToUpdate(idEvento)
            eventoBeforeUpdate = event
        }
    }




    // FUNZIONI CHE SERVONO PER AGGIORNARE I CAMPI DI DETTAGLIO DI UN EVENTO

    fun updateTitle(titolo: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateTitle(titolo, idEvento)
        }
    }
    fun updateCategory(categoria: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateCategory(categoria, idEvento)
        }
    }
    fun updateCitta(citta: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateCitta(citta, idEvento)
        }
    }
    fun updateCosto(costo: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateCosto(costo, idEvento)
        }
    }
    fun updateData(data: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateData(data, idEvento)
        }
    }
    fun updateDescrizione(descrizione: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateDescrizione(descrizione, idEvento)
        }
    }
    fun updateIndirizzo(indirizzo: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateIndirizzo(indirizzo, idEvento)
        }
    }
    fun updateLingue(lingua: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateLingue(lingua, idEvento)
        }
    }
    fun updateNPersone(nPersone: String, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateNPersone(nPersone, idEvento)
        }
    }

    fun updateEventRemote(event: Map<String, String>, idEvento: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.updateEventRemote(event, idEvento)
        }
    }

    // serve per aggiungere una prenotazione ad un determinato evento
    fun addPartecipazione(idEvento: String, partecipazione: Partecipazione) {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.addPartecipazione(idEvento, partecipazione)
        }
    }

}