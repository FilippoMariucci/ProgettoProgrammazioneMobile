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
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
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
        // Inflate the layout for this fragment
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

    private fun getUserEvents() {
        CreateEventsRec.visibility = View.GONE

        vm.getUserEvent(uid)

        val adapter = occasioniCreateAdapter(createdEvents)
        CreateEventsRec.adapter = adapter
        vm.userEvent.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })


        adapter.setOndeleteClickListener(object : occasioniCreateAdapter.OnCreatedClickListener{
            override fun deleteEvent(idEvento: String, size: Int, position: String) {
                eliminaEvento(idEvento, adapter, position)
            }

            override fun modificaEvent(idEvento: String) {
                goModifica(idEvento)
            }
        })
        CreateEventsRec.visibility = View.VISIBLE
    }

    private fun eliminaEvento (idEvento: String, createdAdapter: occasioniCreateAdapter, position: String, )
    {
        // Open dialog to confirm remove action
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure?")
                 .setCancelable(true)
                 .setPositiveButton("Yes", DialogInterface.OnClickListener {
                     dialog, id ->
                     vm.deleteEvent(idEvento, uid)
                     createdAdapter.notifyItemRemoved(position.toInt())
                     dialog.dismiss()
                 })
                .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id-> dialog.cancel()
            })
        val alert = builder.create()
        alert.show()
    }

    private fun goModifica (idEvento: String){
        val bundle = Bundle()
        bundle.putString("idEvento", idEvento)
        Log.d("idEvento", "$idEvento")
        modificaOccasione.arguments = bundle
        fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, modificaOccasione)?.commit()

    }

}