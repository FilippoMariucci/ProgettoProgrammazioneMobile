package com.example.progettoprogrammazionemobile.FirebaseDatabase

import android.util.Log
import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.database.ImageUrlDb
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ImageDataFirebase(private val database: EventsRoomDb) {
    val imageRef = Firebase.storage.reference
    var imageUrls = ArrayList<ImageUrlDb>()

    fun getAllImages() = CoroutineScope(Dispatchers.IO).launch{
        delay(3000L)
        val list = ArrayList<ImageUrlDb>()
        try {
            val images = imageRef.child("Users/").listAll().await()
            for (i in images.items) {
                val url = i.downloadUrl.await()


                val evento_for_image = i.toString().substringAfterLast('/').substringBefore('.')
                val evento_to_change = database.eventoDao().getEventoFromId(evento_for_image)

                val url_singola = ImageUrlDb(url.toString(), evento_for_image)
                list.add(url_singola)
                database.imageDao().insert(url_singola)
                database.eventoDao().update_foto(url.toString(), evento_for_image)

                Log.d("evento_to_change", "${evento_to_change}")
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

    suspend fun set_list(list: ArrayList<ImageUrlDb>)  {
        delay(3000L)
        this.imageUrls = list
    }

    fun get_list() : ArrayList<ImageUrlDb> {
        return this.imageUrls
    }

}