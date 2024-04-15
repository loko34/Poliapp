package com.example.poliapp

import PoliSQLiteOpenHelper
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Videos : Fragment() {

    companion object {
        fun newInstance() = Videos()
    }

    private lateinit var viewModel: VideosViewModel
    private val PICK_VIDEO_REQUEST = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_videos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[VideosViewModel::class.java]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no está concedido, solicitarlo
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        // Configurar el OnClickListener para el botón de cargar video
        val cardView = view?.findViewById<CardView>(R.id.uploadVideo)

        cardView?.setOnClickListener {
            openFileChooser()
        }

    }
    private fun openFileChooser() {
        // Aquí puedes abrir el selector de archivos
        println("doy click")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
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
        /*super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Seleccionar video exitoso, obtener la URI del video seleccionado
            val videoUri = data.data
            // Guardar la URI del video en la base de datos
            saveVideoToDatabase(videoUri)
        }*/

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Seleccionar video exitoso, obtener la URI del video seleccionado
            val videoUri = data.data
            // Guardar la URI del video en la base de datos
            saveVideoToDatabase(videoUri!!)
        }
    }

    private fun saveVideoToDatabase(videoUri: Uri) {
        val dbHelper = PoliSQLiteOpenHelper(requireContext())
        val db = dbHelper.writableDatabase

        // Crear un ContentValues para almacenar los valores a insertar en la base de datos
        val values = ContentValues().apply {
            put("video_uri", videoUri.toString()) // Convertir la URI a String y almacenarla en la columna "video_uri"
        }

        // Insertar los valores en la tabla "Videos"
        val newRowId = db.insert("Videos", null, values)

        // Verificar si la inserción fue exitosa
        if (newRowId != -1L) {
            // Inserción exitosa
            Toast.makeText(requireContext(), "Video guardado en la base de datos", Toast.LENGTH_SHORT).show()
        } else {
            // Error al insertar
            Toast.makeText(requireContext(), "Error al guardar el video en la base de datos", Toast.LENGTH_SHORT).show()
        }

        // Cerrar la conexión a la base de datos
        db.close()
    }
}