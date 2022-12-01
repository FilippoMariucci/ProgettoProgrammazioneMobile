package com.example.progettoprogrammazionemobile.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.progettoprogrammazionemobile.EventsFragments.crea_occasione
import com.example.progettoprogrammazionemobile.model.Evento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class EventoViewModel: ViewModel() {

    private lateinit var reference: DatabaseReference
    private lateinit var storeRef : StorageReference
    private lateinit var imageUri: Uri
    private lateinit var listPartecipanti : ArrayList<String>  // vedere meglio dopo
    lateinit var creaOccasione : crea_occasione  // vedere meglio dopo
    private lateinit var  databaseReferenceEvento: DatabaseReference
    private lateinit var  storageReference: StorageReference
    private lateinit var auth: FirebaseAuth



   //proviamola
    fun saveEvent(event_to_save: Evento) {
            var ritorno = false  //serve per fare un check dopo
            reference = FirebaseDatabase.getInstance().getReference("Evento") //mi metto su questo path
            event_to_save.id_evento = reference.push().getKey(); //prendo idevento e lo uso per fare "qualcosa"

            val url_storage = "gs://programmazionemobile-a1b11.appspot.com/Users/ + ${event_to_save.id_evento}"
            event_to_save.foto = url_storage //setto il campo foto dell'evento

            uploadEventPicture(event_to_save.id_evento) //funzione definita più sotto nella classe

            if (event_to_save.id_evento != null) { //procedo solo se effetivamente cerco di inserire qualcosa che "esiste " e non è nullo
                reference.child(event_to_save.id_evento!!).setValue(event_to_save)
                    .addOnCompleteListener {
                        if (it.isSuccessful) { //se va tutto bene il flag è true
                            ritorno = true
                        }
                    }.addOnFailureListener {
                        ritorno = false  //se non va tutto bene allora il flag è false
                    }
            }
            print(ritorno)  //vedo cosa succede
        } //fine funzione saveevento



    fun setUri(imageUri: Uri){
        this.imageUri = imageUri  //setta la variabile definita all'inizio a quella passata come parametro
    }


     //fa upload sullo storage
    fun uploadEventPicture (idEvento: String ?= null) {
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + idEvento)
        storageReference.putFile(imageUri)
    }

    fun getDateTimeCalendar(): ArrayList<Int> { //stessa funzione usata in altra classe
        val cal = Calendar.getInstance()
        var array = arrayListOf<Int>()  // ci vado a salvare info su una certa data
        var day = cal.get(Calendar.DAY_OF_MONTH)
        var month = cal.get(Calendar.MONTH)
        var year = cal.get(Calendar.YEAR)
        var hour = cal.get(Calendar.HOUR)
        var minute = cal.get(Calendar.MINUTE)
        array.add(day)
        array.add(month)
        array.add(year)
        array.add(hour)
        array.add(minute)
        return array
    }
}