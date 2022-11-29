package com.example.appericolo.ui.preferiti.contacts.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.progettoprogrammazionemobile.model.Evento

@Dao
interface EventoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact : EventoDb)

    @Delete
    fun delete(contact: EventoDb)

    @Query("SELECT * FROM evento_table")
    fun getAllEvents(): LiveData<List<EventoDb>>

    @Query("DELETE FROM evento_table")
    fun deleteAll()

    @Query("UPDATE evento_table SET foto = :url WHERE id_evento = :id_evento")
    fun update_foto(url: String, id_evento: String)

    @Query("SELECT * FROM evento_table WHERE id_evento = :id_evento")
    fun getEventoFromId(id_evento: String): EventoDb

    @Query("SELECT * FROM evento_table WHERE categoria = :titleCat")
    fun filterCategory(titleCat: String): List<EventoDb>

    @Query("DELETE FROM evento_table WHERE id_evento = :id")
    fun deleteFromId(id: String)

    @Query("SELECT * FROM evento_table WHERE userId = :id")
    fun userEvents(id: String): List<EventoDb>

    @Query("SELECT foto FROM evento_table WHERE id_evento = :id")
    fun urlImageFromId(id: String) :String


    /*----------UPDATE FIELDS QUERIES-----------*/
    @Query("UPDATE evento_table SET titolo = :titolo WHERE id_evento = :id_evento")
    fun updateTitle(titolo: String, id_evento: String)
    @Query("UPDATE evento_table SET categoria = :cat WHERE id_evento = :id_evento")
    fun updateCategory(cat: String, id_evento: String)
    @Query("UPDATE evento_table SET citta = :citta WHERE id_evento = :id_evento")
    fun updateCitta(citta: String, id_evento: String)
    @Query("UPDATE evento_table SET costo = :costo WHERE id_evento = :id_evento")
    fun updateCosto(costo: String, id_evento: String)
    @Query("UPDATE evento_table SET data_evento = :data WHERE id_evento = :id_evento")
    fun updateData(data: String, id_evento: String)
    @Query("UPDATE evento_table SET descrizione = :desc WHERE id_evento = :id_evento")
    fun updateDescrizione(desc: String, id_evento: String)
    @Query("UPDATE evento_table SET indirizzo = :indirizzo WHERE id_evento = :id_evento")
    fun updateIndirizzo(indirizzo: String, id_evento: String)
    @Query("UPDATE evento_table SET lingue = :lingua WHERE id_evento = :id_evento")
    fun updateLingue(lingua: String, id_evento: String)
    @Query("UPDATE evento_table SET n_persone = :npers WHERE id_evento = :id_evento")
    fun updateNPersone(npers: String, id_evento: String)

//    @Query("SELECT * FROM evento_table")
//    fun getUserEvent(id_user: String): LiveData<List<EventoDb>>

}