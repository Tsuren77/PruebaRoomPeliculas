package edu.dam.pruebaroompeliculas.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import edu.dam.pruebaroompeliculas.R
import edu.dam.pruebaroompeliculas.adapter.OnPeliculaClickListener
import edu.dam.pruebaroompeliculas.adapter.PeliculaAdapter
import edu.dam.pruebaroompeliculas.database.PeliculaApplication
import edu.dam.pruebaroompeliculas.databinding.FragmentHomeBinding
import edu.dam.pruebaroompeliculas.model.Pelicula
import java.util.concurrent.LinkedBlockingQueue


class HomeFragment : Fragment(), OnPeliculaClickListener {

    private lateinit var binding: FragmentHomeBinding
    lateinit var mAdapter: PeliculaAdapter
    private lateinit var mLinearLayout: StaggeredGridLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycler()
    }

    //mostrar los datos en el recyclerView
    private fun setUpRecycler() {
        mAdapter = PeliculaAdapter(mutableListOf(),this,0)
        mLinearLayout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        getPeliculas() //cargar los datos de la base de datos en la lista

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager= mLinearLayout
            adapter = mAdapter
        }

    }

    //leer los dato de la base de datos y cargarlo en la lista Hilo secundario
    private fun getPeliculas() {
        val queue = LinkedBlockingQueue<MutableList<Pelicula>>()

        Thread{
            val peliculaList = PeliculaApplication.database.peliculaDao().getAllPelicula()
            queue.add(peliculaList)
        }.start()

        mAdapter.setPelicula(queue.take()) //comunicar los cambios al adaptador
    }

    //poner los miembros del onclick
    override fun onItemClick(position: Long) {

        Toast.makeText(requireContext(),"Pulsar mas tiempo", Toast.LENGTH_LONG).show()
    }

    override fun onItemLongClick(pelicula: Pelicula, position: Long) {
        onSetDialog(pelicula, position)
    }

    private fun onSetDialog(pelicula: Pelicula,position: Long) {
        val builder = AlertDialog.Builder(requireContext())
        //inflar
        val customView = layoutInflater.inflate(R.layout.dialog_personal, null)

        //pasar los datos
        builder.setView(customView)

        //crear
        val dialog = builder.create()

        //trabajar con los botones
        val btnEliminar = customView.findViewById<Button>(R.id.buttonEliminar)
        val btnModificar = customView.findViewById<Button>(R.id.buttonModificar)

        //poner los botones a la escucha
        btnEliminar.setOnClickListener {
            onDeletePelicula(pelicula)
            dialog.dismiss()
        }

        btnModificar.setOnClickListener {
           onModificarPelicula(position)
            dialog.dismiss()
        }

        //ejecutar el dialog
        dialog.show()

    }

    //modficar tarea
    private fun onModificarPelicula(position: Long) {
        //llamar al dialog actualizar
       // ActualizarDialogFragment().show(childFragmentManager, "actualFragment")
       // findNavController().navigate(R.id.action_action_home_to_actualizarDialogFragment)
        val action = HomeFragmentDirections.actionActionHomeToActualizarDialogFragment(identificador = position)
        findNavController().navigate(action)

    }

    //borrar tarea confirmando el borrado con un nuevo dialog
    private fun onDeletePelicula(pelicula: Pelicula) {

        //crear la alerta con Builder
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Pelicula") //titulo
            .setMessage("¿Quiere eliminar la Pelicula?") //mensaje
            //boton en positivo con el oyente/accion
            .setPositiveButton("ok"){dialog, which ->

                val queue = LinkedBlockingQueue<Pelicula>()
                Thread{
                   PeliculaApplication.database.peliculaDao().deletePelicula(pelicula)
                    queue.add(pelicula)
                }.start()

                mAdapter.delete(queue.take()) //comunicar los cambios al adaptador
                Toast.makeText(requireContext(),"presionado OK", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel"){dialog, which ->
                Toast.makeText(requireContext(),"presionado Cancel", Toast.LENGTH_SHORT).show()
            }

            .show() //mostrar la alertDialog
    }


}