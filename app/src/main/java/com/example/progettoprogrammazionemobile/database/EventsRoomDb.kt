package com.example.appericolo.ui.preferiti.contacts.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.progettoprogrammazionemobile.database.ImageUrlDb

//entities=arrayOf(Contact::class)
@Database(entities = [EventoDb::class, ImageUrlDb::class], version = 3, exportSchema = false)
abstract class EventsRoomDb : RoomDatabase() {

    abstract fun eventoDao(): EventoDao
    abstract fun imageDao(): ImageUrlDao

    companion object{
        private var INSTANCE: EventsRoomDb? =  null

        fun getDatabase(context: Context): EventsRoomDb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
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