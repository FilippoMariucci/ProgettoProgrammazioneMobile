package com.example.progettoprogrammazionemobile.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData




import com.example.progettoprogrammazionemobile.FirebaseDatabase.ImageDataFirebase
import com.example.progettoprogrammazionemobile.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.database.ImageUrlDb

class ImageRepository(private val database: EventsRoomDb) {

    // per evitare possibili problemi di corrispondenza
    var imageData = ImageDataFirebase(database)
    val imagesUrls: LiveData<List<ImageUrlDb>> = database.imageDao().getAllImagesUrl()
    val imageEvent =  MutableLiveData<ImageUrlDb>()


    //cancellato f.ne precedente

    fun getDataFromRemote() {
        var prova = ArrayList<ImageUrlDb>()

        imageData.getAllImages() //funzione che serve a recuparare immagini

        }




}
