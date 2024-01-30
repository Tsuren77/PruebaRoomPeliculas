package edu.dam.pruebaroompeliculas.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "tablePelicula")
data class Pelicula(
    @PrimaryKey(autoGenerate = true)
    var id: Long =0,
    var fecha_actual: String = "",
    var title: String = "",
    var descripcion: String ="",
    var fecha_nota: String = "",
    var estado: Boolean = false,
    var imagen: String,
    var valoracion: Float  //para el raiting
) : Parcelable
// tecla Alt + Insertar o Code-> Generate solo marcar el id
