package com.example.progettoprogrammazionemobile.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.appericolo.ui.preferiti.contacts.database.EventsRoomDb
import com.example.appericolo.ui.preferiti.contacts.database.ImageUrlDao
import com.example.progettoprogrammazionemobile.Repository.EventsRepository
import com.example.progettoprogrammazionemobile.Repository.ImageRepository
import com.example.progettoprogrammazionemobile.database.ImageUrlDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class imageViewModel(application: Application) : AndroidViewModel(application) {
                     //per mantenere stato globale appllicazione (come trovato nella docs associata a questa classe)
    var readImageData: LiveData<List<ImageUrlDb>>
    lateinit var ImageEvent: LiveData<ImageUrlDb>  //sono livedata perchè sono per leggere
    private val imagesRepository: ImageRepository

    //usato init perche suggeriva ide di usarlo
    init {
        imagesRepository = ImageRepository(EventsRoomDb.getDatabase(application))
        Log.d("images", "${imagesRepository.imagesUrls}")
        readImageData = imagesRepository.imagesUrls
        Log.d("pippoimage", "$readImageData")

    }

    fun getDataFromRemote() {
        viewModelScope.launch(Dispatchers.IO){
            imagesRepository.getDataFromRemote() //serev a recuperare immagini
        }
    }

    fun refreshFeed() {
        this.getDataFromRemote()
    }


}