package com.example.gamevault.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.gamevault.R
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.databinding.ActivityHomeBinding

/**
 * Activity contenedora de las tres pestañas (Inicio, Lista y Cuenta)
 * mediante un único BottomNavigationView conectado al NavController.
 * El detalle del juego se muestra como destino del mismo nav graph y
 * oculta la barra inferior.
 */
class HomeActivity : AppCompatActivity(), FragmentCommunicator {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        // Oculta el bottom navigation cuando se entra al detalle del juego.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id != R.id.gameDetailFragment
        }
    }

    /** Permite a los fragments cambiar de pestaña (ej. empty state). */
    fun goToTab(menuItemId: Int) {
        binding.bottomNav.selectedItemId = menuItemId
    }

    override fun manageLoader(isVisible: Boolean) {
        binding.loader.loaderView.isVisible = isVisible
    }
}
