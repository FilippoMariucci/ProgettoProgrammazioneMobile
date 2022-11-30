package com.example.appericolo.ui.preferiti.contacts.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.progettoprogrammazionemobile.database.ImageUrlDb

//dichiaro un db e le sue tabelle
@Database(entities = [EventoDb::class, ImageUrlDb::class], version = 3, exportSchema = false)
abstract class EventsRoomDb : RoomDatabase() {

    abstract fun eventoDao(): EventoDao
    abstract fun imageDao(): ImageUrlDao

    companion object{
        private var INSTANCE: EventsRoomDb? =  null

        fun getDatabase(context: Context): EventsRoomDb {
            //se istanza non è nulla lo ritorno
               //se è nulla allora creo db
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventsRoomDb::class.java,
                    "evento_table",
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                //return instance
                instance
            }
        }
    }
}