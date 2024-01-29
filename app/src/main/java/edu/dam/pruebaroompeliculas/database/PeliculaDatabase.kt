package edu.dam.pruebaroompeliculas.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.dam.pruebaroompeliculas.model.Pelicula

@Database(entities = arrayOf(Pelicula::class), version = 1)
abstract class PeliculaDatabase : RoomDatabase() {
    abstract fun peliculaDao(): PeliculaDao
}