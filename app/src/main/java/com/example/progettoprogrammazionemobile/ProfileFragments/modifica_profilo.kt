package com.example.progettoprogrammazionemobile.ProfileFragments
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.databinding.FragmentModificaProfiloBinding
import com.example.progettoprogrammazionemobile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
class modifica_profilo : Fragment(R.layout.fragment_modifica_profilo) {
    private var _binding : FragmentModificaProfiloBinding? = null
    private val binding get() = _binding!!
    //private lateinit var binding: FragmentModificaProfiloBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialog : Dialog
    private lateinit var user : User
    private lateinit var uid : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModificaProfiloBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        if(uid.isNotEmpty()){
            getUserData()
        }
    }
    private fun getUserData() {
        databaseReference.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                binding.inputNomeModifica.setText(user.name)
                binding.cognomeInputModifica.setText(user.surname)
                binding.inputStatoModifica.setText(user.state)
                binding.inputDescrizioneModifica.setText(user.description)
                binding.inputDataModifica.setText(user.birth)
                binding.buttModifica.setOnClickListener {
                    val name = binding.inputNomeModifica.text.toString()
                    val surname = binding.cognomeInputModifica.text.toString()
                    val birth = binding.inputDataModifica.text.toString()
                    val state = binding.inputStatoModifica.text.toString()
                    val description = binding.inputDescrizioneModifica.text.toString()
                    if (checkFields(name, surname, birth))  updateData(name, surname, birth, state, description)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun checkFields(textName: String, textSurname: String, textdateOfBirth: String): Boolean {
        if (textName.isEmpty()) {
            binding.inputNomeModifica.setError("Name is required")
            binding.inputNomeModifica.requestFocus()
            return false
        }
        if (textSurname.isEmpty()) {
            binding.cognomeInputModifica.setError("Surname is required")
            binding.cognomeInputModifica.requestFocus()
            return false
        }

        if (textdateOfBirth.isEmpty()) {
            binding.inputDataModifica.setError("Your birth is required")
            binding.inputNomeModifica.requestFocus()
            return false
        }
        else
            return true
    }
    private fun updateData(name: String, surname: String, birth: String, state: String, description: String) {
//val firebaseAuth = FirebaseAuth.getInstance()
//val database = FirebaseDatabase.getInstance()
//val db = database.getReference()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid.toString().trim()
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        var userRef = databaseRef.child(uid)
        val user = mapOf<String,String>(
            "name" to name,
            "surname" to surname,
            "birth" to birth,
            "state" to state,
            "description" to description
        )
        userRef.updateChildren(user).addOnSuccessListener {
            Toast.makeText(this.requireContext(), "Succesfuly updated", Toast.LENGTH_SHORT).show()
            fragmentManager?.beginTransaction()?.replace(R.id.myNavHostFragment, profilo())?.commit()
        }.addOnFailureListener{
            Toast.makeText(this.requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
        }
    }

}