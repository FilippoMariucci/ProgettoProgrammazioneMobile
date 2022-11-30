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
        val textName = binding.nome.text.toString().trim()   //con trim tolgo gli spazi sulle stringhe se sono presenti
        val textSurname = binding.surname.text.toString().trim()
        val textEmail = binding.registrationEmail.text.toString()
        val textState = binding.state.text.toString().trim()
        val textPassword = binding.password.text.toString()
        val textConPassword = binding.passconfirm.text.toString().trim()
        val textdateOfBirth = binding.dateofbirth.text.toString().trim()
        val description = binding.description.text.toString().trim()
        val check = checkFields(textName, textSurname, textEmail, textPassword, textConPassword, textdateOfBirth)
        // con checkfields controllo che tutti i campi rispettino i vincoli decisi: lo uso come flag per registrare account
        // se ha messo tutto bene,sennò prima di registrarlo deve inserire bene TUTTI i campi

        auth = Firebase.auth

        if (check == true) {  //se ha messo tutto bene allora procedo con la fase di registrazione
            val user = User(textName, textSurname, textConPassword, textdateOfBirth, textState, description) //cose che salvo di user che si registra
            auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(this) {
                if (it.isSuccessful) { //se user viene creato  con successo allora lo registro
                    val firebaseUser: FirebaseUser = it.result!!.user!!
                    database.child(firebaseUser.uid).setValue(
                        user)
                    Toast.makeText(this, "You've been succesfully registred!", Toast.LENGTH_LONG).show() //gli dico è andato tutto bene
                    startActivity(Intent(this, Login::class.java))  //lo mando al login con intent
                    finish()
                }
                else{ //se il processo non va a buon fine...
                    Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show()
                }
            }
        }

   }


    //funzione che mi serve per validare i campi della registrazione
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
            binding.passconfirm.setError("Confirm your password please!")
            binding.passconfirm.requestFocus()
            return false

        }
        if (textdateOfBirth.isEmpty()) {
            binding.dateofbirth.setError("Your birth is required")
            binding.dateofbirth.requestFocus()
            return false
        }
        else {
            val year = Calendar.getInstance().get(Calendar.YEAR);  //anno in cui siamo
            val current_year = textdateOfBirth.substringAfterLast('/')  //prendo anno del dato inserito
            if(current_year >= year.toString()) {    //questo non può andar bene
                binding.dateofbirth.setError("this is not possible")
                binding.dateofbirth.requestFocus()
                return false
            }
        }

        if(!textPassword.equals(textPassword)){
            binding.passconfirm.setError("Passwords don't match!")  //se la conferma pass non corrsiponde
            binding.passconfirm.setText(" ")                        // a quella inserita sopra nella form di registrazione
            return false
        }
        else
            return true
    }
}