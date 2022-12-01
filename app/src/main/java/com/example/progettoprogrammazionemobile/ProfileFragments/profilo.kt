package com.example.progettoprogrammazionemobile.ProfileFragments
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.progettoprogrammazionemobile.EventsFragments.occasioni_accettate
import com.example.progettoprogrammazionemobile.EventsFragments.occasioni_create
import com.example.progettoprogrammazionemobile.Login
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.databinding.FragmentProfiloBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class profilo : Fragment()  {

    private var _binding : FragmentProfiloBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var uid : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString().trim()  //ekimina spazi sulla stringa
        _binding = FragmentProfiloBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //sotto i bottoni  che vedo nella schermata "profilo " con le relative azioni

        //per andare su frag di modifica
        val button_mod = binding.modificaprofiloButt
        button_mod.setOnClickListener{
            if(isAdded)  fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, modifica_profilo())?.commit()
        }                                                                                     //mi porta a questo fragment

        //per vedere a cosa ho scelto di partecipare
        val button_occas_accett = binding.occasioniAccettateProf
        button_occas_accett.setOnClickListener{
            fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, occasioni_accettate())?.commit()
        }

        //per vedere eventi che ho creato
        val button_occas_create = binding.occasionicreateProfilo
        button_occas_create.setOnClickListener{
            fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, occasioni_create())?.commit()
        }

        // questo per fare logout e tornare all'activity di login con intent
        binding.btnLogout.setOnClickListener{
            auth.signOut()
            val intent = Intent (getActivity(), Login::class.java)
            getActivity()?.startActivity(intent)
        }
    }
}