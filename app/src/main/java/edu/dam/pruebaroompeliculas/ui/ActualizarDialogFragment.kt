package edu.dam.pruebaroompeliculas.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import edu.dam.pruebaroompeliculas.R
import edu.dam.pruebaroompeliculas.database.PeliculaApplication
import edu.dam.pruebaroompeliculas.databinding.DialogActualizarBinding
import edu.dam.pruebaroompeliculas.model.Pelicula
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.LinkedBlockingQueue

class ActualizarDialogFragment: DialogFragment() , DialogInterface.OnShowListener{

    private val args by navArgs<ActualizarDialogFragmentArgs>()
     private var binding: DialogActualizarBinding? = null
    //botones para actualizar
    private var positiveButton: Button? = null
    private var negativeButton: Button? = null

    private var pelicula: Pelicula? = null
    private val calendar = Calendar.getInstance()
    private var estado: Boolean = false


    //crear el dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            binding = DialogActualizarBinding.inflate(layoutInflater)

            binding?.let {
                val builder = AlertDialog.Builder(requireContext())
                    .setTitle("Modificar Pelicula")
                    .setPositiveButton("Modificar",null)
                    .setNegativeButton("Cancelar", null)
                    .setView(it.root)

                val dialog = builder.create()
                dialog.setOnShowListener(this)

                return dialog
            }

        return super.onCreateDialog(savedInstanceState)
    }

    //miembros de la interface
    override fun onShow(p0: DialogInterface?) {

        //metodo para inicialiar la tarea y poner los datos en el view
        initPelicula()
        ObtenerFechaActual()
        setOnButton()
        //trabajar el spinner
        onSetupSpinner()

        val dialog = dialog as AlertDialog
        dialog.let {
            positiveButton = it.getButton(Dialog.BUTTON_POSITIVE)
            negativeButton = it.getButton(Dialog.BUTTON_NEGATIVE)

            positiveButton?.setOnClickListener {

                //recoger los datos o rellenar el objeto tarea
                pelicula?.title = binding?.editTitulo?.text.toString().trim()
                pelicula?.descripcion = binding?.editDescripcion?.text.toString().trim()
                pelicula?.fecha_nota = binding?.textViewFecha?.text.toString().trim()
                pelicula?.fecha_actual = binding?.textActual?.text.toString().trim()
                pelicula?.estado = estado
                pelicula?.valoracion = binding?.valoracionEstrellas?.rating ?: 0.0f //ESTO SE AÑADIO

                 //metodo para actualizar el registro
                //hilo secundario para actualizar
                val queue = LinkedBlockingQueue<Pelicula>()
                Thread{
                    PeliculaApplication.database.peliculaDao().updatePelicula(pelicula!!)
                    queue.add(pelicula)
                }.start()

                //una vez que se ha terminado de ejecutar el hilo
                with(queue.take()){

                    findNavController().navigate(R.id.action_actualizarDialogFragment_to_action_home)
                    dismiss()
                   Toast.makeText(requireContext(), "Pelicula actualizada", Toast.LENGTH_LONG).show()
                }
            }

            negativeButton?.setOnClickListener {
                Toast.makeText(requireContext(), "Cancelar la modificarion", Toast.LENGTH_LONG).show()
            }

        }
    }


    //localizar por el id los datos de la tarea  y llamar a la fun para mostrar los datos
    private fun initPelicula() {
        val ident = args.identificador

        //recoger los datos y mostrarlos
        val queue = LinkedBlockingQueue<Pelicula?>()

        Thread{
            pelicula = PeliculaApplication.database.peliculaDao().getPeliculaById(ident)
            queue.add(pelicula)
        }.start()

        queue.take()?.let{
            setUiPelicula(it)  //mostrar los dato
        }

    }

    //mostrar los datos en la view
    //pasar los datos del objeto a la view
    private fun setUiPelicula(pelicula: Pelicula) {
        EstadoPelicula(pelicula)

       with(binding) {

           this?.editTitulo!!.setText(pelicula.title)
           this.editDescripcion.setText(pelicula.descripcion)
           this.textActual.setText(pelicula.fecha_actual)
           this.textViewFecha.setText(pelicula.fecha_nota)
          // this.valoracionEstrellas.rating = pelicula.valoracion //SE AÑADIO para valoracion
        }

    }

    private fun setOnButton() {
        binding?.imageCalendar!!.setOnClickListener {
            val datePickerDialog = DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val selectdDate = Calendar.getInstance()
                selectdDate.set(year, monthOfYear, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
                val formattedDate = dateFormat.format(selectdDate.time)
                binding?.textViewFecha?.text = "" + formattedDate
            }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog (requireContext(), datePickerDialog, year, month, day ).show()

        }
    }



    fun ObtenerFechaActual(){
        val fechaRegistro = SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a", Locale.getDefault())
            .format(System.currentTimeMillis())

        binding?.textActual!!.text = fechaRegistro
    }

    fun EstadoPelicula(pelicula: Pelicula){
        if(pelicula.estado ){
            binding?.imgVerde?.visibility = View.VISIBLE
            binding?.textViewEstado?.setText("Pelicula Vista")

        }else{
            binding?.imagRojo?.visibility = View.VISIBLE
            binding?.textViewEstado?.setText("Pelicula por ver")

        }
    }

    private fun onSetupSpinner() {
        val opcion = resources.getStringArray(R.array.EstadoPelicula)
        val spinner = binding?.spinner

        //crear el adapter
        ArrayAdapter.createFromResource(requireContext(), R.array.EstadoPelicula, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = it
        }

        //trabajar la seleccion del spinner
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {

               val estadoSeleccionado = parent?.getItemAtPosition(position).toString()
                if (estadoSeleccionado == "Visto"){
                   // binding?.textViewEstado?.setText(estadoSeleccionado)
                    estado = true
                }else{
                    estado = false
                  //  binding?.textViewEstado?.setText(estadoSeleccionado)
                }
              //  Toast.makeText(this@ActualizarDialogFragment, opcionCiclo, Toast.LENGTH_LONG).show()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }


}