package edu.dam.pruebaroompeliculas.adapter


import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import edu.dam.pruebaroompeliculas.R
import edu.dam.pruebaroompeliculas.databinding.ItemPeliculaBinding
import edu.dam.pruebaroompeliculas.model.Pelicula


class PeliculaAdapter (private var peliculaList: MutableList<Pelicula>, private var listener: OnPeliculaClickListener, private var linea:Int)
    : RecyclerView.Adapter<PeliculaAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_pelicula, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelicula = peliculaList.get(position)

        holder.bind(pelicula)
        if (linea != 1){
            holder.setListener(pelicula)
        }

    }

    override fun getItemCount(): Int {
        return peliculaList.size
    }

    fun setPelicula(peliculaList: MutableList<Pelicula>) {
        this.peliculaList = peliculaList
        notifyDataSetChanged()
    }

    fun delete(pelicula: Pelicula) {
        val index = peliculaList.indexOf(pelicula)
        if (index != -1) {
            peliculaList.removeAt(index)
            notifyItemRemoved(index)
        }
    }


    fun update(pelicula: Pelicula) {
        val index = peliculaList.indexOf(pelicula)
        if (index != -1) {
            peliculaList.set(index, pelicula)
            notifyItemChanged(index)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPeliculaBinding.bind(view)

        fun bind(pelicula: Pelicula) {
            binding.textFecha.text = pelicula.fecha_nota
            binding.textTitulo.text = pelicula.title
            binding.textDescripcion.text = pelicula.descripcion

            //cargar la img si tenemos dato en la tarea, si no que no se visualice el viewimagen
            if(pelicula.imagen.isEmpty()) {
                binding.imgNota.visibility = View.GONE
            }else {
                Glide.with(mContext)
                    .load(Uri.parse(pelicula.imagen))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_access_time) //error
                    .error(R.drawable.ic_broken_image)  //error a cargar la imagen
                    .centerCrop()
                    .into(binding.imgNota)
            }



            if (linea != 1){

                val lineBackground = binding.imgLinea

                if (pelicula.estado == true) {
                    lineBackground.setBackgroundColor(Color.GREEN)
                } else {
                    lineBackground.setBackgroundColor(Color.RED)
                }
            }else{
                binding.imgLinea.visibility = View.GONE
                binding.imgNota.visibility = View.GONE
            }

            binding.valoracionEstrellas.rating = pelicula.valoracion   //SE AÑADIO ESTO




        }

        fun setListener(pelicula: Pelicula) {

            binding.root.setOnClickListener { listener.onItemClick(pelicula.id) }
            binding.root.setOnLongClickListener {
                listener.onItemLongClick(pelicula, pelicula.id)
                true
            }

        }

    }


}