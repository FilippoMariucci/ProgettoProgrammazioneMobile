package com.example.progettoprogrammazionemobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.progettoprogrammazionemobile.EventsFragments.MapsFragment
import com.example.progettoprogrammazionemobile.EventsFragments.crea_occasione
import com.example.progettoprogrammazionemobile.ProfileFragments.profilo
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

//questi sono i fragment su cui voglio navigare tramite bottom app bar
    private val homeFragment = com.example.progettoprogrammazionemobile.homeFragment()
    private val userFragment = profilo()
    private val creaOccasioneFragment = crea_occasione()
    private val mappa = MapsFragment()


    private lateinit var currrentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.beginTransaction().replace(R.id.myNavHostFragment, homeFragment()).commit()
        val bottomNav : BottomNavigationView = findViewById(R.id.bottomAppBar)
        bottomNav.setOnNavigationItemSelectedListener (navListener)

    }

    val navListener = BottomNavigationView.OnNavigationItemSelectedListener{
        when(it.itemId){
            R.id.icon_user -> {
                currrentFragment = userFragment // se sono su user fragment allora in evidenza c'è icona user
            }                                 //stesso ragionamento su fragment successivi e relative icone della bottom app bar
            R.id.icon_add -> {
                currrentFragment = creaOccasioneFragment
            }
            R.id.icon_discover -> {
                currrentFragment = homeFragment
            }
            R.id.icon_map -> {
                currrentFragment = mappa
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.myNavHostFragment, currrentFragment).commit()
        true
    }

}