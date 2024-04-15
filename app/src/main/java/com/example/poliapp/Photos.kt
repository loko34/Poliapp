package com.example.poliapp

import PoliSQLiteOpenHelper
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBindings
import com.example.poliapp.db.contracts.PhotosContract
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Photos : Fragment() {

    companion object {
        fun newInstance() = Photos()
    }
    private lateinit var viewModel: PhotosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        // Obtener referencia al botón
        val btn = view.findViewById<Button>(R.id.BuscarImagen)
        btn?.setOnClickListener {
            loadInformationFromDB()
        }

        view.findViewById<Button>(R.id.BuscarImagen)?.setOnClickListener {
            loadInformationFromDB()
        }

        // Obtener referencia al botón fab_add_photo y asignar el listener
        view.findViewById<FloatingActionButton>(R.id.fab_add_photo)?.setOnClickListener {
            println("Botón presionado")
            findNavController().navigate(R.id.nav_photosadd)
        }
    }
        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
            val button = view?.findViewById<FloatingActionButton>(R.id.fab_add_photo)

            button?.setOnClickListener {
                println("Botón presionado")
                findNavController().navigate(R.id.nav_photosadd)
            }

        }

    private fun loadInformationFromDB(){
        val dbHelper = PoliSQLiteOpenHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.query(PhotosContract.PhotosEntry.TABLE_NAME,
            arrayOf(
                PhotosContract.PhotosEntry.COLUMN_NAME,
                PhotosContract.PhotosEntry.COLUMN_DESCRIPTION,
                PhotosContract.PhotosEntry.COLUMN_IMAGE
            ),null,null,null,null,null
        )

        if(cursor.moveToFirst()){

            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(PhotosContract.PhotosEntry.COLUMN_NAME))
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(PhotosContract.PhotosEntry.COLUMN_DESCRIPTION))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(PhotosContract.PhotosEntry.COLUMN_IMAGE))

            view?.findViewById<TextView>(R.id.DescripcionProducto)?.text  = nombre

            val imagenProductoView =view?.findViewById<ImageView>(R.id.imagenProducto)

            if(!imagePath.isNullOrEmpty()){

                val imagenUri = Uri.parse(imagePath)
                println(imagenUri)
                imagenProductoView?.setImageURI(imagenUri)
            }else{

                imagenProductoView?.setImageResource(R.drawable.photos)
            }

        }
        cursor.close()
        db.close()
    }


}