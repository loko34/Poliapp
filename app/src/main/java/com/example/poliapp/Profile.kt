package com.example.poliapp

import PoliSQLiteOpenHelper
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.poliapp.db.contracts.ProfileContract
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Profile : Fragment() {

    companion object {
        fun newInstance() = Profile()
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var editNames: EditText
    private lateinit var editEmail: EditText
    private lateinit var editOccupation: EditText
    private val PICK_IMAGE_REQUEST = 1
    private var imageProfile = ""
    private lateinit var btnUpdateProfile: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        editNames = view.findViewById(R.id.editNames)
        editEmail = view.findViewById(R.id.editEmail)
        editOccupation = view.findViewById(R.id.editOccupation)
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile)
        progressBar = view.findViewById(R.id.progressBar)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadInformationFromDB();

        val btn = view?.findViewById<Button>(R.id.btnUpdateProfile)

        btn?.setOnClickListener(){
            updateInformationInDB()
        }

        val btnUploadImage = view?.findViewById<ImageButton>(R.id.btnUploadImage)
        btnUploadImage?.setOnClickListener {
            openFileChooser()
        }
    }

    private fun loadInformationFromDB() {
        val dbHelper = PoliSQLiteOpenHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            ProfileContract.ProfileEntry.TABLE_NAME,
            arrayOf(
                ProfileContract.ProfileEntry.COLUMN_NAMES,
                ProfileContract.ProfileEntry.COLUMN_EMAIL,
                ProfileContract.ProfileEntry.COLUMN_OCCUPATION,
                ProfileContract.ProfileEntry.COLUMN_IMAGE
            ),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val names = cursor.getString(cursor.getColumnIndexOrThrow(ProfileContract.ProfileEntry.COLUMN_NAMES))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(ProfileContract.ProfileEntry.COLUMN_EMAIL))
            val occupation = cursor.getString(cursor.getColumnIndexOrThrow(ProfileContract.ProfileEntry.COLUMN_OCCUPATION))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ProfileContract.ProfileEntry.COLUMN_IMAGE))

            view?.findViewById<TextView>(R.id.textViewName)?.text = names
            view?.findViewById<TextView>(R.id.editNames)?.text = names
            view?.findViewById<TextView>(R.id.editEmail)?.text = email
            view?.findViewById<TextView>(R.id.textViewOccupation)?.text = occupation
            view?.findViewById<TextView>(R.id.editOccupation)?.text = occupation
            val imageProfileView = view?.findViewById<ImageView>(R.id.imageProfile)
            if (!imagePath.isNullOrEmpty()) {
                val imageUri = Uri.parse(imagePath)
                println(imageUri)
                imageProfileView?.setImageURI(imageUri)
            } else {
                imageProfileView?.setImageResource(R.drawable.perfil)
            }
        }

        cursor.close()
        db.close()
    }

    private fun updateInformationInDB() {
        editNames.isEnabled = false
        editEmail.isEnabled = false
        editOccupation.isEnabled = false
        btnUpdateProfile.isEnabled = false

        // Show the ProgressBar inside the button and hide the button's text
        progressBar.visibility = View.VISIBLE
        btnUpdateProfile.text = ""

        val newNames = editNames.text.toString()
        val newEmail = editEmail.text.toString()
        val newOccupation = editOccupation.text.toString()
        val newImage = Uri.parse(imageProfile)

        val imagePath = newImage?.let { copyImageToAppStorage(it) }
        updateProfileInDB(newNames, newEmail, newOccupation, imagePath.toString())

        Handler(Looper.getMainLooper()).postDelayed({
            editNames.isEnabled = true
            editEmail.isEnabled = true
            editOccupation.isEnabled = true
            btnUpdateProfile.isEnabled = true

            progressBar.visibility = View.GONE
            btnUpdateProfile.text = "Actualizar"

            loadInformationFromDB()
        }, 2000)
    }

    private fun updateProfileInDB(newNames: String, newEmail: String, newOccupation: String, newImage: String) {
        val dbHelper = PoliSQLiteOpenHelper(requireContext())
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ProfileContract.ProfileEntry.COLUMN_NAMES, newNames)
            put(ProfileContract.ProfileEntry.COLUMN_EMAIL, newEmail)
            put(ProfileContract.ProfileEntry.COLUMN_OCCUPATION, newOccupation)
            put(ProfileContract.ProfileEntry.COLUMN_IMAGE, newImage)
        }

        val selection = "${ProfileContract.ProfileEntry._ID} = ?"
        val selectionArgs = arrayOf("1")

        val rowsUpdated = db.update(
            ProfileContract.ProfileEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        db.close()

        if (rowsUpdated > 0) {
            Toast.makeText(requireContext(), "Actualización exitosa", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyImageToAppStorage(imageUri: Uri): String? {
        val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
        val filename = "profile_image.jpg"
        val outputDir = File(requireContext().filesDir, "profile_images")

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFile = File(outputDir, filename)

        try {
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
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            val imageUri = data.data
            imageProfile = imageUri.toString()
            println(imageProfile)
            val imageView = view?.findViewById<ImageView>(R.id.imageProfile)
            imageView?.setImageURI(imageUri)
        }
    }
}