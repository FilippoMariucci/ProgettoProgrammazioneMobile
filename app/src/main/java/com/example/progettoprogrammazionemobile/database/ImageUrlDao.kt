package com.example.appericolo.ui.preferiti.contacts.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.progettoprogrammazionemobile.database.ImageUrlDb

@Dao
interface ImageUrlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(url : ImageUrlDb)

    @Delete
    fun delete(url: ImageUrlDb)

    @Query("SELECT * FROM image_table")
    fun getAllImagesUrl(): LiveData<List<ImageUrlDb>>

    @Query("DELETE FROM image_table")
    fun deleteAll()

    @Query("DELETE FROM image_table WHERE id_evento =:idevento")
    fun deleteImageFromIdEvento(idevento: String)


}