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
    private lateinit var vm: eventViewModel
    private lateinit var vm_image: imageViewModel
    val adapter = AdapterImageEvent()

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
            refreshFeed()
        }

        // categories
        var categoryimgs = getCategories()

        val recyclerView = binding.categories
        var AdapterCategories = ImageAdapter(categoryimgs)

        recyclerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AdapterCategories

        AdapterCategories.setOnItemClickListener(object : ImageAdapter.onItemClickListener {
            override fun onItemClick(titleCat: String) {
                binding.displayedEvents.setText(titleCat + " events")
                filterEvents(titleCat)
            }
        })


        initialiseObservers()
        fetchAll()
        refreshFeed()

        adapter.setOnItemClickListener(object : AdapterImageEvent.onItemClickListener {
            override fun onItemClick(idevento: String) {
                go_away(idevento)
            }

            override fun skipEvent(posizione: String) {
                val actualPosition = Integer.parseInt(posizione)
                (rv.layoutManager as LinearLayoutManager).scrollToPosition(actualPosition + 1 )
            }

        })
    }


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

    private fun refreshFeed() {
        vm.refreshFeed()
        vm_image.getDataFromRemote()
    }

    private fun filterEvents(titleCat: String) {
        vm.onFilterQuery(titleCat)
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

    fun go_away(idevento: String){
        val bundle = Bundle()
        bundle.putString("idEvento", idevento)
        dettaglioEvento.arguments = bundle
        if(isAdded)  fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, dettaglioEvento)?.commit()
    }
}





