package edu.dam.pruebaroompeliculas.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import edu.dam.pruebaroompeliculas.R
import edu.dam.pruebaroompeliculas.database.PeliculaApplication
import edu.dam.pruebaroompeliculas.databinding.FragmentAddBinding
import edu.dam.pruebaroompeliculas.model.Pelicula
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.LinkedBlockingQueue


class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    private val calendar = Calendar.getInstance()

    private var imageUri: Uri? = null
    private var selectPath: String = ""

    //resultado de PickVisualMedia
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
        if (uri != null){
            //imagen
            val selectdImagenUri = uri
            selectPath = selectdImagenUri.toString()
            //mostrar la imagen en el view
            binding.imagePelicula.setImageURI(uri)

        }else{
            //no imagen seleccionada
            Toast.makeText(requireContext(), "no hay imagen seleccionda", Toast.LENGTH_LONG).show()
        }
    }

    //realizar la foto
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->

        if (result.resultCode == Activity.RESULT_OK){

            val intent = result.data
          //  val imageBitmap = intent?.extras?.get("data") as Bitmap
            val imageBitmap = BitmapFactory.decodeFile(file.toString())
            binding.imagePelicula.setImageBitmap(imageBitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ObtenerFechaActual()
        setOnButton()
        setUpFoto()


        //inflar el menu
        binding.toolbar.inflateMenu(R.menu.pelicula_menu)

        //trabajar con el menu
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_add -> {

                    //realizar el grabado de la nota
                    val pelicula = Pelicula(
                        fecha_actual = binding.textActual.text.toString().trim(),
                        title = binding.editTitulo.text.toString().trim(),
                        descripcion = binding.editDescripcion.text.toString().trim(),
                        fecha_nota = binding.textViewFecha.text.toString().trim(),
                        estado = false,
                        imagen = selectPath,
                        valoracion = binding.valoracionEstrellas.rating //ESTO SE AÑADIO
                    )
                    //hilo secundario para añadir
                    val queue = LinkedBlockingQueue<Long>()
                    Thread{
                        val id =  PeliculaApplication.database.peliculaDao().addPelicula(pelicula)
                        queue.add(id)
                    }.start()

                    //una vez que se ha terminado de ejecutar el hilo
                    with(queue.take()){

                        Toast.makeText(requireContext(),"Pelicula agregada con exito", Toast.LENGTH_LONG).show()

                    }

                    true
                }
                else -> false
            }
        }
    }

    private fun setUpFoto() {
        binding.buttonGaleria.setOnClickListener {
            //coger la foto de la galeria
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.buttonFoto.setOnClickListener {
            //hacer una foto con la camara
           // startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                it.resolveActivity(requireContext().packageManager).also{ component->
                    createPhotoFile()
                    val photoUri: Uri =
                        FileProvider.getUriForFile(
                            requireContext(),
                            "edu.dam.pruebaroompeliculas.fileproviders",
                            file
                        )
                    it.putExtra(MediaStore.EXTRA_OUTPUT,photoUri) //indica donde se va a almacenar la foto

                    selectPath = photoUri.toString()  //para poner los datos en el objeto tarea

                }
            }
            startForResult.launch(intent)

        }
    }

    private lateinit var file: File
   private fun createPhotoFile(){
       val dir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      file = File.createTempFile("IMG_${System.currentTimeMillis()}_",".jpg",dir)
   }

   fun  saveImage(context: Context, id:Long, uri: Uri){
       val file = File(context?.filesDir, id.toString())
       val bytes = context?.contentResolver?.openInputStream(uri)?.readBytes()!!
       file.writeBytes(bytes)
   }


    private fun setOnButton() {
        binding.imageCalendar.setOnClickListener {
            val datePickerDialog = DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val selectdDate = Calendar.getInstance()
                selectdDate.set(year, monthOfYear, dayOfMonth)

                val dateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
                val formattedDate = dateFormat.format(selectdDate.time)
                binding.textViewFecha.text = "" + formattedDate
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

        binding.textActual.text = fechaRegistro
    }


}