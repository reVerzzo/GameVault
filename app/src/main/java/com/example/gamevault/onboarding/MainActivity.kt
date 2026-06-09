package com.example.gamevault.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.databinding.ActivityMainBinding
import com.example.gamevault.home.HomeActivity

/**
 * Activity contenedora del flujo de onboarding (Login, Registro y
 * Datos personales) mediante un NavHostFragment. Si ya hay una sesión
 * activa de Firebase, salta directo al Home.
 */
class MainActivity : AppCompatActivity(), FragmentCommunicator {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si el usuario ya inició sesión antes, vamos directo al Home.
        if (AuthRepository().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun manageLoader(isVisible: Boolean) {
        binding.loader.loaderView.isVisible = isVisible
    }
}
