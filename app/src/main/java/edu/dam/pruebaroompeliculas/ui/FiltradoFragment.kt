package edu.dam.pruebaroompeliculas.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.dam.pruebaroompeliculas.adapter.OnPeliculaClickListener
import edu.dam.pruebaroompeliculas.adapter.PeliculaAdapter
import edu.dam.pruebaroompeliculas.database.PeliculaApplication
import edu.dam.pruebaroompeliculas.databinding.FragmentFiltradoBinding
import edu.dam.pruebaroompeliculas.model.Pelicula
import kotlinx.coroutines.launch


class FiltradoFragment : Fragment(), OnPeliculaClickListener {

    private lateinit var  binding: FragmentFiltradoBinding

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: PeliculaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFiltradoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      //  setupRecyclerView()
        setFilter()
    }

    private fun setFilter() {
        binding.allPeliculas.setOnClickListener{
            getAllPeliculas()
            setupRecyclerView()
        }

        binding.filterVisto.setOnClickListener {
            getVistaPelicula()
            setupRecyclerView()
        }

        binding.filterNoVisto.setOnClickListener {
            getNoVistaPelicula()
            setupRecyclerView()
        }

    }

    private fun getNoVistaPelicula() {
        lifecycleScope.launch {
            val peliculaPendiente = PeliculaApplication.database.peliculaDao().getPeliculaNoVista()
            mAdapter.setPelicula(peliculaPendiente)
        }

    }

    private fun getVistaPelicula() {

        lifecycleScope.launch {
            val peliculaCompleta = PeliculaApplication.database.peliculaDao().getPeliculaVista()
            mAdapter.setPelicula(peliculaCompleta)
        }

    }

    private fun getAllPeliculas() {
        lifecycleScope.launch {
            val peliculaList = PeliculaApplication.database.peliculaDao().getListPelicula()
            mAdapter.setPelicula(peliculaList)
        }
    }

    private fun setupRecyclerView() {
        mAdapter = PeliculaAdapter(mutableListOf(),this,1)
        mLayoutManager = LinearLayoutManager(requireContext())

        binding.recycler.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mAdapter
        }
    }


    //crear los miembros
    override fun onItemClick(position: Long) {
        TODO("Not yet implemented")
    }

    override fun onItemLongClick(pelicula: Pelicula, position: Long) {
        TODO("Not yet implemented")
    }


}