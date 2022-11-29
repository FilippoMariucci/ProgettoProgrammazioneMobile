package com.example.progettoprogrammazionemobile.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.progettoprogrammazionemobile.FirebaseDatabase.ImageDataFirebase
import com.example.progettoprogrammazionemobile.database.ImageUrlDb

class ImageRepository(private val database: EventsRoomDb) {

    // to avoid mismatch problem
    var imageData = ImageDataFirebase(database)
    val imagesUrls: LiveData<List<ImageUrlDb>> = database.imageDao().getAllImagesUrl()
    val imageEvent =  MutableLiveData<ImageUrlDb>()

    fun getDataFromRemote() {
        var prova = ArrayList<ImageUrlDb>()

        imageData.getAllImages()
//        prova = imageData.get_list()
//        Log.d("imagesprova", "$prova")
//        for (image in prova){
//            database.imageDao().insert(image)
        }




}
