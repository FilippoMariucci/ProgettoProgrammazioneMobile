package com.example.progettoprogrammazionemobile

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.progettoprogrammazionemobile.ViewModel.eventViewModel
import com.example.progettoprogrammazionemobile.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.auth.ActionCodeEmailInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //inizializziamo il binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registrationRoot.setOnClickListener(){
            startActivity(Intent(this, Registration::class.java)) //per andare su registrazione
        }
        binding.loginbutton.setOnClickListener{ loginFunction()}

        // con queste linee vediamo se utente è gia loggato e in tal caso lo faccio andare
        //direttamente su homeactivity
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser !=null){startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun loginFunction() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString().trim()
        val check = checkFields(email, password)  //flag per vedere se ha inserito bene

        auth = FirebaseAuth.getInstance()

        if(check == true){  // se ha inserito bene mail e pass vado oltre sennò non inizio per niente questa funzione
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                if(it.isSuccessful){ // se è gia registrato un utente con queste mail e pass allora faccio login e lo mando a home
                    val user = auth.currentUser
                    val userReference = database?.child(user?.uid!!)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                else{  // se non esiste già utente con quella mail allora si deve prima registrare e non posso mandarlo alla home
                    Toast.makeText(this, "You're not registred yet", Toast.LENGTH_LONG).show()
                }
            }
        }
    }  //fine f.ne di login


    private fun checkFields(textEmailInfo: String, pass:String): Boolean {
        if(textEmailInfo.isEmpty()) {
            binding.email.setError("Email field is empty")
            binding.email.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(textEmailInfo).matches()) {
            binding.email.setError("Email missing @!")  // gli dico che manca proprio "identificatore" di una vera mail
            binding.email.requestFocus()
            return false
        }

        if(pass.isEmpty()){
            binding.password.setError("Password field is empty")
            binding.password.requestFocus()
            return false
        }
        else
            return true
    }
}