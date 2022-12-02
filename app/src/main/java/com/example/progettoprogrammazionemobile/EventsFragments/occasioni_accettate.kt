package com.example.progettoprogrammazionemobile.EventsFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progettoprogrammazionemobile.AdapterRV.occasioniAccettateAdapter
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.databinding.FragmentOccasioniAccettateBinding
import com.example.progettoprogrammazionemobile.model.Evento
import com.example.progettoprogrammazionemobile.model.Partecipazione
import com.example.progettoprogrammazionemobile.ProfileFragments.profilo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception
import kotlin.collections.ArrayList


class occasioni_accettate : Fragment() {
    private lateinit var AcceptedEventsRec : RecyclerView
    private lateinit var AcceptedEventsUser : ArrayList<Evento>
    private lateinit var PartecipazioneUser : ArrayList<Partecipazione>
    private val dettaglioEventoAccettato  = dettaglio_evento_accettato()
    private lateinit var key : String
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    private var _binding: FragmentOccasioniAccettateBinding? = null
    private val binding get() = _binding!!

    private var key_array = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        // Inflating del layout
        _binding = FragmentOccasioniAccettateBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AcceptedEventsRec = binding.recyclerOccasioniAccettate
        AcceptedEventsRec.layoutManager = LinearLayoutManager(this.requireContext())
        AcceptedEventsRec.setHasFixedSize(true)
        AcceptedEventsUser = arrayListOf<Evento>()

        getEventsKey()
    }


    //serve per recuperare chiavi degli eventi a cui user si è iscritto per partecipare (--->ha prenotato)
    private fun getEventsKey(){
        val key_events = ArrayList<String>()   //poi mi ci salvo id degli eventi che mi interessano
        var lista_partecipanti = ArrayList<String> ()
        FirebaseDatabase.getInstance().getReference("Partecipazione").
        addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                 var partecipazioni_list = snapshot.children
                    partecipazioni_list.forEach {
                        if (it.child("id_partecipante").getValue() != null) {
                        try {
                            lista_partecipanti =
                                it.child("id_partecipante").getValue() as ArrayList<String>
                            val size = lista_partecipanti.size
                            for (i in 0..size - 1) {  //scorro tutte le partecipazioni perchè voglio cercare dove idpartecipante==user attualmente loggato
                                if (lista_partecipanti[i] != null) {
                                    if (lista_partecipanti[i] == uid) { //se id di user loggato corrsipsonde a quello di un partecipante
                                        key = it.key.toString()  //allora mi interessa evento di cui leggo i partecipanti
                                        key_events.add(key)  //e mi salvo la sua chiave (di quel determinato evento)
                                        Log.d("key", "$key_events")
                                    }
                                }
                            }  //alla fine del for ho ritrovato le chiavi di tutti gli eventi a cui user loggato si è iscritto come partecipante
                        }catch (e : Exception){ Log.d(" problema", "  ee")}
                        }
                    }
                getUserFavouriteEvent(key_events)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //per ritrovare gli eventi a cui si è prenotato user: cosi gli faccio vedere la lista di tutti questi eventi
    private fun getUserFavouriteEvent(key : ArrayList<String>) {
        AcceptedEventsRec.visibility = View.GONE
        AcceptedEventsUser.clear()
        val Eventi = FirebaseDatabase.getInstance().getReference("Evento")
        //val mario = key.size  //messa come un  commento perchè non la uso mai dopo alcune modifiche fatte

        Eventi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (eventSnap in snapshot.children) {
                        val eventoSingolo = eventSnap.getValue(Evento::class.java)
                        for(i in key) { //ciclo tutti gli eventi passati tramite la variabile key
                            if (eventoSingolo != null) { //se ovviamente non è nullo procedo...
                                if(eventoSingolo.id_evento == i) { //se corrsipondono idevento con quelli tra le prenotazioni dell'user loggato
                                    AcceptedEventsUser.add(eventoSingolo)
                                }
                            }
                        }
                    }
                    val adapter = occasioniAccettateAdapter(AcceptedEventsUser)
                    AcceptedEventsRec.adapter = adapter
                    AcceptedEventsRec.visibility = View.VISIBLE
                  //uso adapter per gestire la rappresentazione  degli elementi che compongono la lista (item)
                    adapter.setOnEventClickListener(object : occasioniAccettateAdapter.OnEventClickListener{
                        override fun cancelclick(idEvento: String, size: Int, position: String) { //per andare a eliminare una partecipazione: click su una figura apposita all'interno della "card" che contiene un item della lista
                            var IndexList = ArrayList<String>()
                                FirebaseDatabase.getInstance().getReference("Partecipazione")
                                    .child(idEvento).child("id_partecipante").get()
                                    .addOnSuccessListener {
                                        IndexList = it.value as ArrayList<String>
                                        val index = IndexList.indexOf(uid)
                                        deletePartecipazione(idEvento, position,  adapter, index) //funzione definita dopo
                                    }                                                       //per eliminare prenotazione ad un evento a cui mi ero prenotato
                        }

                        override fun seeMoreclick(idEvento: String, toString: String) {
                            go_Dettaglio(idEvento) //definita dopo per andare a vedere info complete di un evento a cui mi sono prenotato
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    //serve se voglio eliminare la mia prenotazione ad un evento--->non partecipo più a quell'evento
    private fun deletePartecipazione(idevento: String, position: String, occasioniAccettateAdapter: occasioniAccettateAdapter, index: Int)  {
        var flag : Boolean = false
        Log.d("builderprova", "entrato")
        val builder = AlertDialog.Builder(requireActivity())
        Log.d("builderprova", "$builder")
        builder.setMessage("Are you sure?") //gli chiedo se è sicuro di voler eliminare la sua partecipazione a questo evento
            .setCancelable(true)
            .setPositiveButton("Yes", DialogInterface.OnClickListener {  //bottone "positive" del dialog che mi si apre-->con questo elimino la mia partecipazione
                    dialog, id ->
                Log.d("builderprova", "entrato")
                FirebaseDatabase.getInstance()
                    .getReference("Partecipazione").child(idevento)
                    .child("id_partecipante").child(index.toString())
                    .removeValue()  //lo elimino effettivamente
                val position = position.toInt()
                occasioniAccettateAdapter.notifyItemRemoved(position)
                dialog.dismiss()
                fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, profilo())?.commit()  //torno a questo fragment
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { //se ci ripesno e NON voglio eliminare la mia partecipazione
                    dialog, id-> dialog.cancel()
            })
        val alert = builder.create()
        alert.show()
    }


    //per vedere tutte le info di un determinato evento a cui mi sono già prenotato
    //infatti nella lista che vedo subito ci sono solo info parziali
    //per vedere tutte le info devo navigare su un altro fragment
    fun go_Dettaglio(idevento: String){
        val bundleOccasioni = Bundle()
        bundleOccasioni.putString("idEventoAccettato", idevento)
        dettaglioEventoAccettato.arguments = bundleOccasioni
        if(isAdded)  fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, dettaglioEventoAccettato)?.commit()
    }                                                                   //vado a finire su questo fragment
}