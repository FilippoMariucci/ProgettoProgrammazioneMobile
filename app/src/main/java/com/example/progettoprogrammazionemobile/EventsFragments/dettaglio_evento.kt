package com.example.progettoprogrammazionemobile.EventsFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.progettoprogrammazionemobile.ViewModel.eventViewModel
import com.example.progettoprogrammazionemobile.ViewModel.imageViewModel
import com.example.progettoprogrammazionemobile.databinding.FragmentDettaglioEventoBinding
import com.example.progettoprogrammazionemobile.model.Evento
import com.example.progettoprogrammazionemobile.model.Partecipazione
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.event_item.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class dettaglio_evento : Fragment() {

    private lateinit var idEvento : String

    private lateinit var databaseReferenceUser: DatabaseReference
    private lateinit var databaseReferencePartecipazione: DatabaseReference

    private lateinit var listPartecipanti : ArrayList<String>

    private lateinit var evento : Evento
    private var _binding : FragmentDettaglioEventoBinding? = null
    private val binding get() = _binding!!
    private lateinit var urlImageEvento : String

    private lateinit var vm: eventViewModel
    private lateinit var vm_image: imageViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating del  layout
        val args = this.arguments
        val argsEvento = args?.get("idEvento")

        idEvento = argsEvento.toString()
        _binding = FragmentDettaglioEventoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Evento")
        getEventData()
        vm = ViewModelProviders.of(requireActivity()).get(eventViewModel::class.java)
        vm_image = ViewModelProviders.of(requireActivity()).get(imageViewModel::class.java)

    }


    private fun getEventData() {
        var partecipanti = 0
        // take event from local db
        databaseReferenceUser.child(idEvento).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    evento = snapshot.getValue(Evento::class.java)!!
                    Log.d("eventofoto", "${evento.foto}")
                    getImage(idEvento)

                    binding.titoloEventoDett.setText(evento.titolo)
                    binding.dataDett.setText(evento.data_evento)
                    binding.indirizzoDett.setText(evento.indirizzo)
                    binding.linguaEventoDett.setText(evento.lingue)
                    binding.categoriaEventoDett.setText(evento.categoria)
                    binding.cittaDett.setText(evento.citta)
                    binding.indirizzoDett.setText(evento.indirizzo)
                    binding.descEventoDett.setText(evento.descrizione)

                    val npersone = evento.n_persone?.toInt()
                    FirebaseDatabase.getInstance().getReference("Partecipazione").child(idEvento).get().addOnSuccessListener{
                        try {
                            val listPartecipanti = it.child("id_partecipante").getValue() as ArrayList<String>
                            val size = listPartecipanti.size
                            partecipanti = 0
                            for (i in 0..size - 1) {
                                if (listPartecipanti[i] != null) partecipanti += 1  //cosi arrivo a trovare il num di partecipanti
                            }
                        }catch (e : Exception){
                            partecipanti = 0
                        }

                        val persone = npersone?.minus(partecipanti)
                        if (persone != null) {
                            binding.npersone.setText(persone.toString())
                        }
                        if(persone == 0) { //se sono già occupati tutti i posti prenotabili
                            binding.buttonPartecipoDett.visibility = View.INVISIBLE
                            binding.fullEventoText.visibility = View.VISIBLE
                            binding.fullEventoText.setText("Raggiunto il numero massimo di partecipanti")
                        }
                        else{ //se posso ancora prenotarmi allora mi prenoto
                            binding.buttonPartecipoDett.setOnClickListener{
                                partecipaEvento()
                            }
                        }
                    }


                }catch (e: Exception) {
                    binding.titoloEventoDett.setText("L'evento è stato eliminato")
                    binding.buttonPartecipoDett.visibility = View.INVISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Database error", Toast.LENGTH_SHORT).show()

            }
        })
    }

    //serve per recuperare immagine di un certo evento
    private fun getImage(idEvento: String) = CoroutineScope(Dispatchers.IO).launch{
            var image_url = ""
            try {
                val images =  Firebase.storage.reference.child("Users/").listAll().await()
                for (i in images.items) { //tra tutte le immagini su storage cerco quella che corrisponde all'evento passatocome parametro
                    val evento_for_image = i.toString().substringAfterLast('/').substringBefore('.') //prendo una sub stringa che mi identifica evento
                    if(evento_for_image == idEvento){
                        val url = i.downloadUrl.await()
                        image_url = url.toString()
                        Glide.with(requireContext()).load(image_url).into(binding.fotoEvento)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                }
            }
            withContext(Dispatchers.Main) {
                Glide.with(requireContext()).load(image_url).into(binding.fotoEvento)
            }
        } //fine funzione getimage



    private fun partecipaEvento(){
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid.toString().trim()
        val id_evento = evento.id_evento
        val id_creatore = evento.userId
        val id_partecipante = uid

        listPartecipanti = ArrayList<String>()

        //se mi voglio prenotare ad un evento che ho creato io non posso farlo e ricordo questa cosa con un bel messaggio
        if(id_creatore.equals(id_partecipante)) {
            Toast.makeText(requireContext(), "You can't apply for your own event", Toast.LENGTH_SHORT).show()
            return
        }  //se evento è stato creato da un altro utente allora posso prenotarmi
        else {
            databaseReferencePartecipazione = FirebaseDatabase.getInstance().getReference("Partecipazione")

            var loadArray = FirebaseDatabase.getInstance().getReference("Partecipazione").child(idEvento)
            loadArray.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listPartecipanti.clear()
                    try {
                            var nome = snapshot.child("id_partecipante").getValue() as ArrayList<String>
                            val size = nome.size
                            for (i in 0..size - 1) {
                                listPartecipanti.add(nome[i])
                            }
                    }
                    catch (e:Exception) {
                        Toast.makeText(requireContext(), "Sei il primo a partecipare!", Toast.LENGTH_SHORT).show()
                    }
                    //se mi sono già prenotato (e risulta ancora la mia partecipazione perche non mi sono cancellato)non posso prenotarmi di nuovo
                    if (listPartecipanti.contains(id_partecipante)) Toast.makeText(requireContext(), "You can't apply for this event", Toast.LENGTH_SHORT).show()
                    else {
                        listPartecipanti.add(id_partecipante)
                        val partecipazione = Partecipazione(id_creatore, listPartecipanti)

                        if (id_evento != null) { //se mi prenoto effettivamente ad un evento con id non nullo allora procedo
                            databaseReferencePartecipazione.child(id_evento)
                                .setValue(partecipazione)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Your application for this event was succesful!",
                                        Toast.LENGTH_LONG
                                    ).show()  //se va tutto bene gli dico che si è prenotato
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Sorry we got troubles!",
                                        Toast.LENGTH_LONG
                                    ).show()  //se va male qualcosa segnalo questo altro messaggio
                                }
                        }  //fine del blocco --->  if(id_evento != null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    } //fine funzione per prenotarsi ad un evento al quale si vuole partecipare

}