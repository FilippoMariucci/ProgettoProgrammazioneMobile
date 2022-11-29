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

        //remeber that we are gonna initializa biding before settinf the content view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registrationRoot.setOnClickListener(){
            startActivity(Intent(this, Registration::class.java))
        }
        binding.loginbutton.setOnClickListener{ loginFunction()}

        // KEEP USER LOGG
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser !=null){startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun loginFunction() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString().trim()
        val check = checkFields(email, password)

        auth = FirebaseAuth.getInstance()

        if(check == true){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                if(it.isSuccessful){
                    val user = auth.currentUser
                    val userReference = database?.child(user?.uid!!)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "You're not registred yet", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun checkFields(textEmailInfo: String, pass:String): Boolean {
        if(textEmailInfo.isEmpty()) {
            binding.email.setError("Email field is empty")
            binding.email.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(textEmailInfo).matches()) {
            binding.email.setError("Email missing @!")
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