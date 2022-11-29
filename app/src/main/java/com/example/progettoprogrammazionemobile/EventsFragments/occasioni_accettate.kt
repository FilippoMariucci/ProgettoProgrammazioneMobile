package com.example.progettoprogrammazionemobile.EventsFragments

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
        // Inflate the layout for this fragment
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

    private fun getEventsKey(){
        val key_events = ArrayList<String>()
        var lista_partecipanti = ArrayList<String> ()
        FirebaseDatabase.getInstance().getReference("Partecipazione").
        addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var partecipazioni_list = snapshot.children
                    partecipazioni_list.forEach {
                        if (it.child("id_partecipante").getValue() != null) {
                        try {
                            lista_partecipanti =
                                it.child("id_partecipante").getValue() as ArrayList<String>
                            val size = lista_partecipanti.size
                            for (i in 0..size - 1) {
                                if (lista_partecipanti[i] != null) {
                                    if (lista_partecipanti[i] == uid) {
                                        key = it.key.toString()
                                        key_events.add(key)
                                        Log.d("key", "$key_events")
                                    }
                                }
                            }
                        }catch (e : Exception){ Log.d(" gyughgh", "  ee")}
                        }
                    }
                getUserFavouriteEvent(key_events)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getUserFavouriteEvent(key : ArrayList<String>) {
        AcceptedEventsRec.visibility = View.GONE
        AcceptedEventsUser.clear()
        val Eventi = FirebaseDatabase.getInstance().getReference("Evento")
        val mario = key.size

        Eventi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (eventSnap in snapshot.children) {
                        val eventoSingolo = eventSnap.getValue(Evento::class.java)
                        for(i in key) {
                            if (eventoSingolo != null) {
                                if(eventoSingolo.id_evento == i) {
                                    AcceptedEventsUser.add(eventoSingolo)
                                }
                            }
                        }
                    }
                    val adapter = occasioniAccettateAdapter(AcceptedEventsUser)
                    AcceptedEventsRec.adapter = adapter
                    AcceptedEventsRec.visibility = View.VISIBLE

                    adapter.setOnEventClickListener(object : occasioniAccettateAdapter.OnEventClickListener{
                        override fun cancelclick(idEvento: String, size: Int, position: String) {
                            var IndexList = ArrayList<String>()
                                FirebaseDatabase.getInstance().getReference("Partecipazione")
                                    .child(idEvento).child("id_partecipante").get()
                                    .addOnSuccessListener {
                                        IndexList = it.value as ArrayList<String>
                                        val index = IndexList.indexOf(uid)
                                        deletePartecipazione(idEvento, position,  adapter, index)
                                    }
                        }

                        override fun seeMoreclick(idEvento: String, toString: String) {
                            go_Dettaglio(idEvento)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun deletePartecipazione(idevento: String, position: String, occasioniAccettateAdapter: occasioniAccettateAdapter, index: Int)  {
        var flag : Boolean = false
        Log.d("builderprova", "entrato")
        val builder = AlertDialog.Builder(requireActivity())
        Log.d("builderprova", "$builder")
        builder.setMessage("Are you sure?")
            .setCancelable(true)
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id ->
                Log.d("builderprova", "entrato")
                FirebaseDatabase.getInstance()
                    .getReference("Partecipazione").child(idevento)
                    .child("id_partecipante").child(index.toString())
                    .removeValue()
                val position = position.toInt()
                occasioniAccettateAdapter.notifyItemRemoved(position)
                dialog.dismiss()
                fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, profilo())?.commit()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id-> dialog.cancel()
            })
        val alert = builder.create()
        alert.show()
    }

    fun go_Dettaglio(idevento: String){
        val bundleOccasioni = Bundle()
        bundleOccasioni.putString("idEventoAccettato", idevento)
        dettaglioEventoAccettato.arguments = bundleOccasioni
        if(isAdded)  fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, dettaglioEventoAccettato)?.commit()
    }
}