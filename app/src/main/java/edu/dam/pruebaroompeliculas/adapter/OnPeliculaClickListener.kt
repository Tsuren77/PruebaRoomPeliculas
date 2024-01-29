package edu.dam.pruebaroompeliculas.adapter

import edu.dam.pruebaroompeliculas.model.Pelicula

interface OnPeliculaClickListener {

    fun onItemClick(position: Long)
    fun onItemLongClick(pelicula: Pelicula,position: Long)

}
