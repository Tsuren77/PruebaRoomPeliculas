package edu.dam.pruebaroompeliculas.database

import android.app.Application
import androidx.room.Room

class PeliculaApplication: Application() {

    companion object{
        lateinit var database: PeliculaDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this,
            PeliculaDatabase::class.java,
            "PeliculaDatabase")
            .build()
    }
}