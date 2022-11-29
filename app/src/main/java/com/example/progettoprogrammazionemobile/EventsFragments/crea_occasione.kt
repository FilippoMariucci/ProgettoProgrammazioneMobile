package com.example.progettoprogrammazionemobile.EventsFragments


import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.progettoprogrammazionemobile.BuildConfig
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.ViewModel.eventViewModel
import com.example.progettoprogrammazionemobile.databinding.FragmentCreaOccasioneBinding
import com.example.progettoprogrammazionemobile.homeFragment
import com.example.progettoprogrammazionemobile.model.Evento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


class crea_occasione : Fragment(R.layout.fragment_crea_occasione), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private lateinit var auth: FirebaseAuth
    private lateinit var uid : String
    private var _binding: FragmentCreaOccasioneBinding? = null
    private val binding get() = _binding!!
    private lateinit var  databaseReference: DatabaseReference
    private lateinit var imageUri: Uri
    private  var button : Button ?= null
    private  var imageView: ImageView ?= null
    private val home = homeFragment()
    private lateinit var getPosition : List<Address>

    private var packageName = BuildConfig.APPLICATION_ID

    private lateinit var evento : Evento
    private lateinit var vm: eventViewModel

    var array_date_time = arrayListOf<Int>()
    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    companion object{
        private val IMAGE_REQUEST_CODE = 100
        private val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /* SET DATA E ORA DIALOG ----------
    -----------------------------------
     */
    override fun onResume() {
        super.onResume()
        val languages = resources.getStringArray(R.array.languages)
        val categories = resources.getStringArray(R.array.categories)
        val arrayLanguagesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, languages)
        val arrayCategoriesAdapter = ArrayAdapter(requireContext(),
            R.layout.dropdown_item, categories)
        binding.autoCompleteCategories.setAdapter(arrayCategoriesAdapter)
        binding.autoCompleteLanguages.setAdapter(arrayLanguagesAdapter)
        binding.InputDataEvento.setOnClickListener(View.OnClickListener {
            array_date_time = vm.getDateTimeCalendar()
            DatePickerDialog(requireContext(), this, array_date_time.get(2), array_date_time.get(1), array_date_time.get(0)).show()
        })
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayofMonth: Int) {
        savedDay = dayofMonth
        savedMonth = (month+1)
        savedYear = year

        array_date_time = arrayListOf<Int>()
        array_date_time = vm.getDateTimeCalendar()
        TimePickerDialog(requireContext(), this, array_date_time.get(3), array_date_time.get(4), true).show()
    }
    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute

        binding.textDateEvento.text = "$savedDay-${savedMonth}-$savedYear at $savedHour:$savedMinute"
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout XML file and return a binding object instance
        _binding= FragmentCreaOccasioneBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProviders.of(requireActivity()).get(eventViewModel::class.java)

        button = getView()?.findViewById(R.id.scegliImmagine)
        imageView = getView()?.findViewById(R.id.immagine)

        button?.setOnClickListener {
            activity?.let{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ){
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, PERMISSION_CODE)
                    } else{
                        pickImagegallery()
                    }
                }else{
                    pickImagegallery()
                }

            }
        }

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnaddEvento.setOnClickListener{
            this.saveEvento()
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImagegallery()
                }else{
                    Toast.makeText(this.requireContext(),"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun pickImagegallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            imageView?.setImageURI(data?.data)
            imageUri = data?.data!!
            vm.setUri(imageUri)
        }
    }

    // function that check fields
    private fun saveEvento(){
        val geocode = Geocoder(requireContext())

        val idEvento = ""
        val titolo_evento = binding.titoloEvento.editText?.text.toString().trim()
        if(titolo_evento.isEmpty()){binding.errorMsg.setText("Aggiungi un titolo all'evento!"); return}
        val descrizione_evento = binding.DescrizioneEvento.editText?.text.toString().trim()
        if(descrizione_evento.isEmpty()){binding.errorMsg.setText("Aggiungi una descrizione all'evento!"); return}
        val lingue_evento = binding.autoCompleteLanguages.text.toString().trim()
        val citta_evento = binding.CittaEvento.editText?.text.toString().trim()

        if(lingue_evento.isEmpty()){binding.errorMsg.setText("Aggiungi una lingua che parlerete all'evento!"); return}
        if(citta_evento.isEmpty()){binding.errorMsg.setText("Aggiungi la città dell'evento!"); return}

        val indirizzo_evento = binding.indirizzoEvento.editText?.text.toString().trim()
        if(indirizzo_evento.isEmpty()){binding.errorMsg.setText("Aggiungi l'indirizzo dell'evento!"); return}

        // check fields inputs
        try {
            var indirizzoEvento = "$indirizzo_evento" + ", " + "$citta_evento"
            getPosition = geocode.getFromLocationName(indirizzoEvento, 5)
            if (getPosition.isEmpty()) {
                binding.errorMsg.setText("Citta o indirizzo errati, attento a non inserire spazi alla fine!"); return}
        }catch (e: Exception){
            Log.d("getPosition", "$e"); return}

        val npersone_evento = binding.npersoneEvento.editText?.text.toString().trim()
        if(npersone_evento.isEmpty() ){
            binding.errorMsg.setText("Aggiungi il numero di persone richiesto per l'evento!"); return
        }else {
            try {npersone_evento.toInt()}
            catch (e:Exception) {
                binding.errorMsg.setText("Il numero di persone deve essere un numero! ;)"); return
            }
        }
        val costo_evento = binding.prezzoEvento.editText?.text.toString().trim()
        if(costo_evento.isEmpty()){
            binding.errorMsg.setText("Aggiungi il costo dell'evento!"); return
        }else {
            try {costo_evento.toFloat()}
            catch (e:Exception) {
                binding.errorMsg.setText("Il prezzo deve essere un numero! ;)"); return
            }
        }
        val data_evento = binding.textDateEvento.text.toString().trim()
        if(data_evento.isEmpty()){
            binding.errorMsg.setText("Aggiungi la data e l'ora dell'evento"); return
        }else{
            if(savedYear < array_date_time.get(2)){
               binding.errorMsg.setText("Aggiungi una data a partire da domani"); return
            }
            else if(savedYear == array_date_time.get(2) && savedMonth < array_date_time.get(1)+1) {
                    binding.errorMsg.setText("Aggiungi una data a partire da domani"); return
            }
            else if(savedYear == array_date_time.get(2) && savedMonth == array_date_time.get(1)+1 && savedDay <= array_date_time.get(0)) {
                binding.errorMsg.setText("Aggiungi una data a partire da domani"); return
            }
        }
        val foto_evento : String
        try{foto_evento = imageUri.toString().trim()}
        catch (e: Exception){binding.errorMsg.setText("Aggiungi un'immagine dell'evento"); return}
        val categoria_evento = binding.autoCompleteCategories.text.toString().trim()
        if(categoria_evento.isEmpty()){binding.errorMsg.setText("Aggiungi la categoria dell'evento"); return}
        val userId = uid

        val model= EventoDb(idEvento, titolo_evento, descrizione_evento, lingue_evento,
            categoria_evento, citta_evento, indirizzo_evento, data_evento, costo_evento,
            npersone_evento, foto_evento, userId)
        vm.saveEvento(model)
        Toast.makeText(requireContext(), "Evento creato con Successo!", Toast.LENGTH_SHORT).show()
        backDiscover()
    }

    private fun backDiscover() {
        if(isAdded)  fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, home)?.commit()
    }
}