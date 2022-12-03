package com.example.progettoprogrammazionemobile.EventsFragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.progettoprogrammazionemobile.database.EventoDb


import com.example.progettoprogrammazionemobile.AdapterRV.occasioniCreateAdapter
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.ViewModel.eventViewModel
import com.example.progettoprogrammazionemobile.databinding.FragmentOccasioniCreateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class occasioni_create : Fragment() {

    private lateinit var CreateEventsRec: RecyclerView
    private lateinit var createdEvents: List<EventoDb>
    private val modificaOccasione = modifica_occasione()
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var vm: eventViewModel


    private var _binding: FragmentOccasioniCreateBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating del layout del fragment
        _binding = FragmentOccasioniCreateBinding.inflate(inflater, container, false,)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProviders.of(requireActivity()).get(eventViewModel::class.java)

        CreateEventsRec = binding.recyclerOccasioniCreate
        CreateEventsRec.layoutManager = LinearLayoutManager(this.requireContext())
        CreateEventsRec.setHasFixedSize(true)

        createdEvents = emptyList<EventoDb>()

        getUserEvents()

        binding.btnAddEvent.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, crea_occasione())
                ?.commit()
        }
    }


    //vedo lista degli eventi creati e posso modificare dati oppure eliminare evento
    private fun getUserEvents() {
        CreateEventsRec.visibility = View.GONE

        vm.getUserEvent(uid)

        val adapter = occasioniCreateAdapter(createdEvents)
        CreateEventsRec.adapter = adapter
        vm.userEvent.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })


        adapter.setOndeleteClickListener(object : occasioniCreateAdapter.OnCreatedClickListener{
            override fun deleteEvent(idEvento: String, size: Int, position: String) { //per eliminare un evento
                eliminaEvento(idEvento, adapter, position)  //definita dopo
            }
               //per andare a modificare dettagli di un evento
            override fun modificaEvent(idEvento: String) {
                goModifica(idEvento)  //definita dopo
            }
        })
        CreateEventsRec.visibility = View.VISIBLE
    }


    //per eliminare un evento che user loggato aveva creato
    private fun eliminaEvento (idEvento: String, createdAdapter: occasioniCreateAdapter, position: String, )
    {
        // faccio comparire un dialog per fargli confermare/interrompere  l'azione di eliminazione
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure?")
                 .setCancelable(true)
                 .setPositiveButton("Yes", DialogInterface.OnClickListener { //se vuole eliminare
                     dialog, id ->
                     vm.deleteEvent(idEvento, uid)
                     createdAdapter.notifyItemRemoved(position.toInt())
                     dialog.dismiss()
                 })
                .setNegativeButton("No", DialogInterface.OnClickListener { //se non vuole eliminare
                    dialog, id-> dialog.cancel()
            })
        val alert = builder.create()
        alert.show()
    }


    //per andare a modificare un evento che user loggato aveva in precedenza creato
    private fun goModifica (idEvento: String){
        val bundle = Bundle()
        bundle.putString("idEvento", idEvento)
        Log.d("idEvento", "$idEvento")
        modificaOccasione.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, modificaOccasione)?.commit()
                                                              //vado in questo fragment per poi effetivamente fare le modifiche che voglio
    }

}