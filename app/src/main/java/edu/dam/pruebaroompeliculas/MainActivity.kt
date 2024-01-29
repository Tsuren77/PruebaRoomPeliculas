package edu.dam.pruebaroompeliculas
//https://www.youtube.com/watch?v=1N6xmCHZexo


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.dam.pruebaroompeliculas.adapter.PeliculaAdapter
import edu.dam.pruebaroompeliculas.databinding.ActivityMainBinding
import edu.dam.pruebaroompeliculas.ui.HomeFragment

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private var mHomeFrag : HomeFragment? = null
    private lateinit var mAdapter: PeliculaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.setupWithNavController(navController)
    }


}