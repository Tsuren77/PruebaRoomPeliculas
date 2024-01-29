package edu.dam.pruebaroompeliculas.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.dam.pruebaroompeliculas.model.Pelicula

@Dao
interface PeliculaDao {

    @Query("SELECT * FROM tablePelicula")
    fun getAllPelicula() : MutableList<Pelicula>

    @Query("SELECT * FROM tablePelicula where id = :id")
    fun getPeliculaById(id: Long): Pelicula

    @Insert
    fun addPelicula(pelicula: Pelicula): Long

    @Update
    fun updatePelicula(pelicula: Pelicula)

    @Delete
     fun deletePelicula(pelicula: Pelicula)

    //realizar las fun con conrutines
    @Query("SELECT * FROM tablePelicula")
    suspend fun getListPelicula(): MutableList<Pelicula>

    @Query("SELECT * FROM tablePelicula WHERE estado " )
    suspend fun getPeliculaVista(): MutableList<Pelicula>

    @Query("SELECT * FROM tablePelicula WHERE not estado " )
    suspend fun getPeliculaNoVista(): MutableList<Pelicula>

}