package com.example.poliapp
//ok
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

@Suppress("UNREACHABLE_CODE")
class web : Fragment() {

    companion object {
        fun newInstance() = web()
    }

    private lateinit var viewModel: WebViewModel

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web, container, false)

        val buscarButton: Button = view.findViewById(R.id.BuscarUrl)
        val editTextUrl: EditText = view.findViewById(R.id.editTextUrl)

        buscarButton.setOnClickListener {
            val url = editTextUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                val webpage: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "No se puede abrir el enlace.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor ingresa una URL.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
           }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WebViewModel::class.java)
        // TODO: Use the ViewModel
    }

}