package com.example.poliapp

import java.util.*
import PoliSQLiteOpenHelper
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Build
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat
import androidx.core.os.postDelayed
import androidx.navigation.fragment.findNavController
import com.example.poliapp.db.contracts.PhotosContract
import com.example.poliapp.db.contracts.ProfileContract
import java.io.File
import java.io.FileOutputStream


@Suppress("UNREACHABLE_CODE")
class PhotosAdd : Fragment() {

    companion object {
        fun newInstance() = PhotosAdd()
    }
    private lateinit var viewModel: PhotosAddViewModel
    private lateinit var nombreImg : EditText
    private lateinit var descripcionImg: EditText
    private var image =""
    private lateinit var BotonGuardarImg : Button
    private lateinit var progressBar: ProgressBar
    private val PICK_IMAGE_REQUEST = 1



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      val view= inflater.inflate(R.layout.fragment_photos_add, container, false)
        nombreImg= view.findViewById(R.id.NomImagen)
        descripcionImg = view.findViewById(R.id.DescripcionImagen)
        BotonGuardarImg = view.findViewById(R.id.BotonGuardarImagen)
        progressBar = view.findViewById(R.id.progressBar)

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val cardView = view?.findViewById<CardView>(R.id.uploadPhoto)

        cardView?.setOnClickListener {
            openFileChooser()
        }

        val btnSave = view?.findViewById<Button>(R.id.BotonGuardarImagen)
        btnSave?.setOnClickListener {
            saveInformationInBD()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                // Permiso denegado
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            image = imageUri.toString()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun saveInformationInBD(){

        val newNombreImg = nombreImg.text.toString().trim()
        val newDescripcionImg = descripcionImg.text.toString().trim()

        // Verificar si los campos no están vacíos
        if (newNombreImg.isNotEmpty() && newDescripcionImg.isNotEmpty() && image.isNotEmpty()) {
            nombreImg.isEnabled=false
            descripcionImg.isEnabled=false
            BotonGuardarImg.isEnabled=false

            progressBar.visibility=View.VISIBLE
            BotonGuardarImg.text=""

            val newImage = Uri.parse(image)
            val imagePath = newImage?.let { copyImageToAppStorage(it) }

            updatePhotosInDB(newNombreImg,newDescripcionImg,imagePath.toString())
        } else {
            Toast.makeText(requireContext(), "Por favor ingresa información en todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePhotosInDB(newNombreImg: String,newDescripcionImg:String,newImage :String){

        val dbHelper= PoliSQLiteOpenHelper(requireContext())
        val db= dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put(PhotosContract.PhotosEntry.COLUMN_NAME,newNombreImg)
            put(PhotosContract.PhotosEntry.COLUMN_DESCRIPTION,newDescripcionImg)
            put(PhotosContract.PhotosEntry.COLUMN_IMAGE, newImage)
        }

        val rowsInsert = db.insert(PhotosContract.PhotosEntry.TABLE_NAME, null, valores)

        if (rowsInsert != -1L) {
            Toast.makeText(requireContext(), "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show()
        }

        nombreImg.isEnabled=true
        descripcionImg.isEnabled=true
        BotonGuardarImg.isEnabled=true
        progressBar.visibility=View.GONE
        db.close()
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.nav_photos)
        }, 2000)
    }

    fun generateRandomFilename(): String {
        val random = Random()
        val randomSuffix = random.nextInt(10000) // Cambia el rango según sea necesario
        return "imagen-$randomSuffix.png"
    }

    private fun copyImageToAppStorage(imageUri: Uri): String? {
        try {
            val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
            val randomFilename = generateRandomFilename()
            val outputDir = File(requireContext().filesDir, "photos")

            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val outputFile = File(outputDir, randomFilename)

            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            return outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    // Este metodo no es necesaria aqui sino en en fragment de photos
    private fun loadInformationFromDB(){
        val dbHelper =PoliSQLiteOpenHelper(requireContext())
        val db= dbHelper.readableDatabase

        val cursor =db.query(
            PhotosContract.PhotosEntry.TABLE_NAME,
            arrayOf(
                PhotosContract.PhotosEntry.COLUMN_NAME,
                PhotosContract.PhotosEntry.COLUMN_DESCRIPTION,
                PhotosContract.PhotosEntry.COLUMN_IMAGE
            ),null,null,null,null,null
        )
        if(cursor.moveToFirst()){

            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(PhotosContract.PhotosEntry.COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(PhotosContract.PhotosEntry.COLUMN_DESCRIPTION))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(PhotosContract.PhotosEntry.COLUMN_IMAGE))

                println("Nombre: $name, Descripción: $description Imagen: $image" )

            } while (cursor.moveToNext())

        }
        cursor.close()
        db.close()
    }
}