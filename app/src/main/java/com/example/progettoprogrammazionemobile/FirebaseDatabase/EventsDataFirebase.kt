package com.example.progettoprogrammazionemobile.FirebaseDatabase

import android.net.Uri
import android.util.Log



import com.example.progettoprogrammazionemobile.database.EventoDb
import com.example.progettoprogrammazionemobile.database.EventsRoomDb


import com.example.progettoprogrammazionemobile.model.Partecipazione
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//qui prendo riferimenti al db firebase
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
        return this.eventList  //lista eventi  con le info ritornata ritornata
    }

    fun boh(): Boolean {
        getEvents()  //funzione definita dopo
        return true
    }

    //qui uso le coroutine per eseguire in modo asincrono e non bloccare il thread principale
    //visto che leggo dal db e può metterci tempo
    fun getAllEvents()  = CoroutineScope(Dispatchers.IO).launch{
        dbRef = FirebaseDatabase.getInstance().getReference("Evento")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (eventSnap in snapshot.children) {
                        val eventoSingolo = eventSnap.getValue(EventoDb::class.java)
                        if (eventoSingolo != null) {                      //se diverso da null c'è qualcosa e
                            database.eventoDao().insert(eventoSingolo)  //li salvo nel db room
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


  //la uso sopra in altra funzione
    fun getEvents() {
        databaseRemoteEvents.get().addOnSuccessListener {
            events ->
            val list = ArrayList<EventoDb>()
            for(evento in events.children){
                val eventoSingolo = evento.getValue(EventoDb::class.java)

            Log.d("prova1", "$eventoSingolo")
                list.add(eventoSingolo!!)
            Log.d("prova2", "$list")
            }
            setList(list)  //funzione definita dopo
        }.addOnFailureListener{
            Log.d("prova3", "errore")
        }
        Log.d("prova4", "${eventList}")
        Thread.sleep(3000)
    }  //fine getevents

    private fun setList(event: ArrayList<EventoDb>){
        this.eventList = event
        Log.d("riuscitoo", "${eventList}")
    }

    fun inserEventRemote(model: EventoDb, imageUri: Uri) {
            var ritorno = false

            model.id_evento = databaseRemoteEvents.push().getKey().toString();
           //da rivedere meglio

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
            print(ritorno)  //per verificare se è andato a buon fine
    }

    fun uploadEventPictureRemote (idEvento: String? = null, imageUri: Uri) {
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + idEvento)
        storageReference.putFile(imageUri)
    }

    fun deleteFromRemote(evento_to_delete: EventoDb) {
        databaseRemoteEvents.child(evento_to_delete.id_evento).removeValue()
        databaseRemotePartecipazione.child(evento_to_delete.id_evento).removeValue()


        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(evento_to_delete.foto)
        storageReference.delete()
    }

    fun updateEventOnRemote(event: Map<String, String>, idEvento: String) {
        databaseRemoteEvents.child(idEvento).updateChildren(event)
    }

    fun addPartecipazioneRemote(idEvento: String, partecipazione: Partecipazione) { //scrivo su firebase
        databaseRemotePartecipazione.child(idEvento).setValue(partecipazione)
    }


}