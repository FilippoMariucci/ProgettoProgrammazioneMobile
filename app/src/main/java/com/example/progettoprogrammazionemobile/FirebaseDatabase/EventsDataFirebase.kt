package com.example.progettoprogrammazionemobile.FirebaseDatabase

import android.net.Uri
import android.util.Log
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.model.Partecipazione
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventsDataFirebase(private val database: EventsRoomDb) {
    var eventList = ArrayList<EventoDb>()
    var auth = FirebaseAuth.getInstance()
    private lateinit var  storageReference: StorageReference
    var databaseRemoteEvents: DatabaseReference = FirebaseDatabase.getInstance("https://programmazionemobile-a1b11-default-rtdb.firebaseio.com/")
        .getReference("Evento")
    var databaseRemotePartecipazione: DatabaseReference = FirebaseDatabase.getInstance(
        "https://programmazionemobile-a1b11-default-rtdb.firebaseio.com/")
        .getReference("Partecipazione")

    lateinit var dbRef : DatabaseReference

    fun getList(): ArrayList<EventoDb> {
        Log.d("getlist", "${this.eventList}")
        return this.eventList
    }

    fun boh(): Boolean {
        getEvents()
        return true
    }

    fun getAllEvents()  = CoroutineScope(Dispatchers.IO).launch{
        dbRef = FirebaseDatabase.getInstance().getReference("Evento")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (eventSnap in snapshot.children) {
                        val eventoSingolo = eventSnap.getValue(EventoDb::class.java)
                        if (eventoSingolo != null) {
                            database.eventoDao().insert(eventoSingolo)
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    fun getEvents() {
        databaseRemoteEvents.get().addOnSuccessListener {
            events ->
            val dio = ArrayList<EventoDb>()
            for(evento in events.children){
                val eventoSingolo = evento.getValue(EventoDb::class.java)
//                if (eventoSingolo != null) {
//                    database.eventoDao().insert(eventoSingolo)
//                }
            Log.d("fica1", "$eventoSingolo")
                dio.add(eventoSingolo!!)
            Log.d("diocane", "$dio")
            }
            setList(dio)
        }.addOnFailureListener{
            Log.d("erroreFirebase", "errore")
        }
        Log.d("fica", "${eventList}")
        Thread.sleep(3000)
    }

    private fun setList(eventdio: ArrayList<EventoDb>){
        this.eventList = eventdio
        Log.d("hovinto", "${eventList}")
    }

    fun inserEventRemote(model: EventoDb, imageUri: Uri) {
            var ritorno = false
            //reference = FirebaseDatabase.getInstance().getReference("Evento")
            model.id_evento = databaseRemoteEvents.push().getKey().toString();

//            val url_storage = "gs://programmazionemobile-a1b11.appspot.com/Users/${model.id_evento}"
//            model.foto = url_storage

            uploadEventPictureRemote(model.id_evento, imageUri)

            if (model.id_evento != null) {
                databaseRemoteEvents.child(model.id_evento!!).setValue(model)
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

    fun uploadEventPictureRemote (idEvento: String? = null, imageUri: Uri) {
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + idEvento)
        storageReference.putFile(imageUri)
    }

    fun deleteFromRemote(evento_to_delete: EventoDb) {
        databaseRemoteEvents.child(evento_to_delete.id_evento).removeValue()
        databaseRemotePartecipazione.child(evento_to_delete.id_evento).removeValue()

        // delete image from storage
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(evento_to_delete.foto)
        storageReference.delete()
    }

    fun updateEventOnRemote(event: Map<String, String>, idEvento: String) {
        databaseRemoteEvents.child(idEvento).updateChildren(event)
    }

    fun addPartecipazioneRemote(idEvento: String, partecipazione: Partecipazione) {
        databaseRemotePartecipazione.child(idEvento).setValue(partecipazione)
    }


}