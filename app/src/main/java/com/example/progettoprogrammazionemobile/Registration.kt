package com.example.progettoprogrammazionemobile

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progettoprogrammazionemobile.databinding.ActivityRegistrationBinding
import com.example.progettoprogrammazionemobile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*


class Registration : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private var database = FirebaseDatabase.getInstance().getReference("Users")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registrationButton.setOnClickListener{ registrationFunction() }

    }

    private fun registrationFunction() {
        val textName = binding.nome.text.toString().trim()
        val textSurname = binding.surname.text.toString().trim()
        val textEmail = binding.registrationEmail.text.toString()
        val textState = binding.state.text.toString().trim()
        val textPassword = binding.password.text.toString()
        val textConPassword = binding.passconfirm.text.toString().trim()
        val textdateOfBirth = binding.dateofbirth.text.toString().trim()
        val description = binding.description.text.toString().trim()
        val check = checkFields(textName, textSurname, textEmail, textPassword, textConPassword, textdateOfBirth)

        auth = Firebase.auth

        if (check == true) {
            val user = User(textName, textSurname, textConPassword, textdateOfBirth, textState, description)
            auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val firebaseUser: FirebaseUser = it.result!!.user!!
                    database.child(firebaseUser.uid).setValue(
                        user)
                    Toast.makeText(this, "You've been succesfully registred!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show()
                }
            }
        }

   }

    private fun checkFields(textName: String, textSurname: String, textEmail: String, textPassword: String, textConPassword: String, textdateOfBirth: String): Boolean {
        if (textEmail.isEmpty()) {
            binding.registrationEmail.setError("email is required")
            binding.registrationEmail.requestFocus()
            return false
        }

        if (textName.isEmpty()) {
            binding.nome.setError("Name is required")
            binding.nome.requestFocus()
            return false
        }
        if (textSurname.isEmpty()) {
            binding.surname.setError("Surname is required")
            binding.surname.requestFocus()
            return false

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            binding.registrationEmail.setError("Email missing @!")
            binding.registrationEmail.requestFocus()
            return false
        }

        if (textPassword.isEmpty()) {
            binding.password.setError("obv Password is required")
            binding.nome.requestFocus()
            return false
        }

        if(textPassword.length<6){
            binding.password.setError("obv Password MUST BE AT LEAST 6 CHARACTERS")
            binding.nome.requestFocus()
        }

        if (textConPassword.isEmpty()) {
            binding.passconfirm.setError("Confirm your password please")
            binding.passconfirm.requestFocus()
            return false

        }
        if (textdateOfBirth.isEmpty()) {
            binding.dateofbirth.setError("Your birth is required")
            binding.dateofbirth.requestFocus()
            return false
        }
        else {
            val year = Calendar.getInstance().get(Calendar.YEAR);
            val current_year = textdateOfBirth.substringAfterLast('/')
            if(current_year >= year.toString()) {
                binding.dateofbirth.setError("Are you an alien?")
                binding.dateofbirth.requestFocus()
                return false
            }
        }

        if(!textPassword.equals(textPassword)){
            binding.passconfirm.setError("Passwords don't match!")
            binding.passconfirm.setText(" ")
            return false
        }
        else
            return true
    }
}