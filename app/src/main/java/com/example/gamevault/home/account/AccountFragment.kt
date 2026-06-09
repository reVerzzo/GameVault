package com.example.gamevault.home.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.content.Intent
import com.example.gamevault.R
import com.example.gamevault.core.ResponseService
import com.example.gamevault.databinding.FragmentAccountBinding
import com.example.gamevault.onboarding.MainActivity
import com.example.gamevault.onboarding.personal.model.UserProfile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCerrarSesion.setOnClickListener { confirmLogout() }
        binding.btnConfiguracion.setOnClickListener {
            Snackbar.make(binding.root, "Configuración próximamente", Snackbar.LENGTH_SHORT).show()
        }

        observeProfile()
        observeStats()
        viewModel.loadProfile()
        viewModel.loadStats()
    }

    private fun observeStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesCount.collect { count ->
                    binding.tvStatsCount.text =
                        resources.getQuantityString(R.plurals.account_stats_count, count, count)
                }
            }
        }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que quieres salir?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Cerrar sesión") { _, _ ->
                viewModel.logout()
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .show()
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> bindProfile(state.data)
                        is ResponseService.Error ->
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        else -> {}
                    }
                }
            }
        }
    }

    /** Pinta la información de la cuenta guardada en el registro. */
    private fun bindProfile(profile: UserProfile) {
        val fullName = profile.fullName().ifBlank { "Gamer" }
        binding.tvUserName.text = fullName
        binding.tvUserEmail.text = profile.email
        binding.imgAvatar.text = fullName.take(1).uppercase()
        binding.tvFullName.text = fullName
        binding.tvPhone.text = profile.phone.ifBlank { "Sin registrar" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
