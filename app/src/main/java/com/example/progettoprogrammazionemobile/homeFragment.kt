package com.example.progettoprogrammazionemobile

import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progettoprogrammazionemobile.AdapterRV.AdapterImageEvent
import com.example.progettoprogrammazionemobile.AdapterRV.ImageAdapter
import com.example.progettoprogrammazionemobile.EventsFragments.dettaglio_evento
import com.example.progettoprogrammazionemobile.ViewModel.eventViewModel
import com.example.progettoprogrammazionemobile.ViewModel.imageViewModel
import com.example.progettoprogrammazionemobile.databinding.FragmentHomeBinding
import com.example.progettoprogrammazionemobile.model.Evento
import com.example.progettoprogrammazionemobile.model.category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*


class homeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var vm: eventViewModel //mi richiamo i view model per gestire la lista degli eventi e per le immagini associate agli eventi
    private lateinit var vm_image: imageViewModel
    val adapter = AdapterImageEvent()  //serve per lavorare con le liste

    private lateinit var eventsRec: RecyclerView
    private lateinit var eventList: ArrayList<Evento>
    private lateinit var dbRef: DatabaseReference
    private val dettaglioEvento = dettaglio_evento()
    val imageRef = Firebase.storage.reference
    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val mapEventsBitMap = mutableMapOf<String, Bitmap>()
    val imagesUrl = listOf<String>()

    private var _binding: com.example.progettoprogrammazionemobile.databinding.FragmentHomeBinding? =
        null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        vm = ViewModelProviders.of(requireActivity()).get(eventViewModel::class.java)
        vm_image = ViewModelProviders.of(requireActivity()).get(imageViewModel::class.java)
        val rv = view.findViewById<RecyclerView>(R.id.rvEvents)
        rv.adapter = adapter
        rv.layoutManager = myLinearLayoutManager
        rv.setHasFixedSize(true)

        binding.refreshBtn.setOnClickListener {
            refreshFeed()  //lego la funzione di refresh,scritta dopo, al button della view
        }

        // mi serve per recuperare vector asset per le categorie con f.ne scritta dopo
        var categoryimgs = getCategories()

        val recyclerView = binding.categories
        var AdapterCategories = ImageAdapter(categoryimgs)

        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AdapterCategories

        AdapterCategories.setOnItemClickListener(object : ImageAdapter.onItemClickListener {
            override fun onItemClick(titleCat: String) {
                binding.displayedEvents.setText(titleCat + " events")  //prima di "events" sullo schermo
                filterEvents(titleCat)                                 // legge di quale categoria specifica si tratta la lista
            }                                                          //che scorre
        })


        initialiseObservers()
        fetchAll()
        refreshFeed()

        adapter.setOnItemClickListener(object : AdapterImageEvent.onItemClickListener {
            override fun onItemClick(idevento: String) {
                go_away(idevento)  //vedo un detrminato evento identificato dal suo id
            }

            override fun skipEvent(posizione: String) { //per scorrere la lista andando avanti di una posizione rispetto a quella corrente
                val actualPosition = Integer.parseInt(posizione)
                (rv.layoutManager as LinearLayoutManager).scrollToPosition(actualPosition + 1 )
            }

        })
    }

  //per far vedere il vector asset che vogliamo su ogni categoria accanto alla scritta che la identifica
    private fun getCategories(): List<category> {
        var categoryimgs = listOf<category>(
            category(R.drawable.ic_baseline_airplanemode_active_24, "Adventure"),
            category(R.drawable.ic_baseline_create_24, "Art"),
            category(R.drawable.ic_baseline_music_note_24, "Concert"),
            category(R.drawable.ic_baseline_sports_soccer_24, "Sport"),
            category(R.drawable.ic_baseline_photo_camera_24, "Photo"),
            category(R.drawable.ic_baseline_groups_2_24, "Role Games"),
            category(R.drawable.ic_baseline_cake_24, "Party")
        )
        return categoryimgs
    }

    private fun refreshFeed() { //per fare refresh della schermata visualizzata
        vm.refreshFeed()
        vm_image.getDataFromRemote()
    }

    private fun filterEvents(titleCat: String) {
        vm.onFilterQuery(titleCat) //uso metodo del viewmodel per effettuare una ricerca tramite tipo di categoria
    }

    private fun initialiseObservers() {
        vm.filterEventsLiveData.observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
    }

    fun fetchAll() {
            vm.readEventData.observe(requireActivity(), Observer { contact ->
                adapter.setData(contact)
                adapter.notifyDataSetChanged()
                Log.d("aggiunta", "${vm.readEventData}")
            })
    }

    fun go_away(idevento: String){ //per andare a vedere i dettagli di un determinato evento tramite il suo id
        val bundle = Bundle()
        bundle.putString("idEvento", idevento)
        dettaglioEvento.arguments = bundle
        if(isAdded)  fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, dettaglioEvento)?.commit()
    }                                                                      // mi porta al fragment di dettaglio
}





