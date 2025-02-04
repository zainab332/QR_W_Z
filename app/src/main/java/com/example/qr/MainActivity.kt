package com.example.qr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser le BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Charger le fragment par défaut (ScannerFragment) au démarrage de l'application
        if (savedInstanceState == null) {
            loadFragment(ScannerFragment())
            bottomNavigationView.selectedItemId = R.id.scanner // Sélectionner l'élément Scanner
        }

        // Gestion des clics dans le BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.scanner -> {
                    loadFragment(ScannerFragment()) // Afficher ScannerFragment
                    true
                }
                R.id.Creer -> {
                    loadFragment(CreerFragment()) // Afficher le fragment "Créer"
                    true
                }
                R.id.historique -> {
                    loadFragment(HistoriqueFragment()) // Afficher le fragment "Historique"
                    true
                }
                R.id.Parametre -> {
                    loadFragment(ParametreFragment()) // Afficher le fragment "Paramètre"
                    true
                }
                else -> false
            }
        }
    }

    // Fonction pour remplacer le fragment dans le container
    private fun loadFragment(fragment: Fragment) {
        // Utiliser une transaction pour remplacer le fragment dans le FrameLayout
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
