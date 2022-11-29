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
    private lateinit var listPartecipanti : ArrayList<String>
    lateinit var creaOccasione : crea_occasione
    private lateinit var  databaseReferenceEvento: DatabaseReference
    private lateinit var  storageReference: StorageReference
    private lateinit var auth: FirebaseAuth



    fun saveEvent(event_to_save: Evento) {
            var ritorno = false
            reference = FirebaseDatabase.getInstance().getReference("Evento")
            event_to_save.id_evento = reference.push().getKey();

            val url_storage = "gs://programmazionemobile-a1b11.appspot.com/Users/ + ${event_to_save.id_evento}"
            event_to_save.foto = url_storage

            uploadEventPicture(event_to_save.id_evento)

            if (event_to_save.id_evento != null) {
                reference.child(event_to_save.id_evento!!).setValue(event_to_save)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            ritorno = true
                        }
                    }.addOnFailureListener {
                        ritorno = false
                    }
            }
            print(ritorno)
        }

    fun setUri(imageUri: Uri){
        this.imageUri = imageUri
    }


    // upload su storage
    fun uploadEventPicture (idEvento: String ?= null) {
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + idEvento)
        storageReference.putFile(imageUri)
    }

    fun getDateTimeCalendar(): ArrayList<Int> {
        val cal = Calendar.getInstance()
        var array = arrayListOf<Int>()
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