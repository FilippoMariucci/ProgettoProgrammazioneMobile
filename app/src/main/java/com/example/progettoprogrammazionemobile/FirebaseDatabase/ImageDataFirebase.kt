package com.example.progettoprogrammazionemobile.FirebaseDatabase

import android.util.Log
//import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.database.EventoDb
import com.example.progettoprogrammazionemobile.database.EventsRoomDb


import com.example.progettoprogrammazionemobile.database.ImageUrlDb
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ImageDataFirebase(private val database: EventsRoomDb) {
    val imageRef = Firebase.storage.reference
    var imageUrls = ArrayList<ImageUrlDb>()


    //uso coroutine per esecuzione asincrona e per  non bloccare l' app se ci mette troppo a leggere da firebase
    fun getAllImages() = CoroutineScope(Dispatchers.IO).launch{
        delay(3000L) //è una suspend fun che ritarda la coroutine per un dato tempo senza bloccare il thread
        val list = ArrayList<ImageUrlDb>()
        try {
            val images = imageRef.child("Users/").listAll().await() //si setta su questo path firebase e prende tutti items presenti
            for (i in images.items) { //e poi li cicla con il for
                val url = i.downloadUrl.await()


                val evento_for_image = i.toString().substringAfterLast('/').substringBefore('.')  //prende una certa porzione della stringa in pratica
                val evento_to_change = database.eventoDao().getEventoFromId(evento_for_image) //prende un certo evento tramite id
                                                                                              //estrapolato sopra

                val url_singola = ImageUrlDb(url.toString(), evento_for_image) //imageurldb () è un costruttore in imagedao--> url e id evento sono della singola entità image
                list.add(url_singola) //aggiunge il parametro passato(che è una entità image) alla variabile list che è arraylist<imageurldb>
                database.imageDao().insert(url_singola) //inserisce la foto in room
                database.eventoDao().update_foto(url.toString(), evento_for_image) //fa update della foto per un certo evento (update perchè all'inizio su eventodb la foto è null nel costruttore)
                                             //questa è url della foto //questo è evento tramite id

                Log.d("evento_to_change", "${evento_to_change}") //servivano per dei controlli
                Log.d("evento_for_image", "${evento_for_image}")
            }
            Log.d("imagelist", "${list}")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("getlist", "${e.message}")
            }
        }
        withContext(Dispatchers.Main) {
            Log.d("imagelist", "${imageUrls}")
            set_list(list)
        }
    }


    //funzione suspend per esecuzione asincrona
    suspend fun set_list(list: ArrayList<ImageUrlDb>)  {
        delay(3000L)
        this.imageUrls = list  //setta variabile definita all'inizio a list che è definita dentro getallimages
    }

    fun get_list() : ArrayList<ImageUrlDb> {
        return this.imageUrls
    }

}