package com.example.gamevault.onboarding.personal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.core.ResponseService
import com.example.gamevault.databinding.FragmentPersonalInfoBinding
import com.example.gamevault.home.HomeActivity
import com.example.gamevault.onboarding.personal.model.UserProfile
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PersonalInfoViewModel>()
    private lateinit var communicator: FragmentCommunicator
    private val auth = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.etFechaNacimiento.setOnClickListener { showDatePicker() }
        binding.tilFechaNacimiento.setEndIconOnClickListener { showDatePicker() }
        binding.btnContinuar.setOnClickListener { attemptSave() }

        observeSave()
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona tu fecha de nacimiento")
            .build()
        picker.addOnPositiveButtonClickListener { selection ->
            // El picker entrega el tiempo en UTC.
            val utc = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            binding.etFechaNacimiento.setText(utc.format(Date(selection)))
            binding.tilFechaNacimiento.error = null
        }
        picker.show(childFragmentManager, "date_picker")
    }

    private fun attemptSave() {
        val nombre = binding.etNombre.text?.toString().orEmpty().trim()
        val apellidos = binding.etApellidos.text?.toString().orEmpty().trim()
        val celular = binding.etCelular.text?.toString().orEmpty().trim()
        val fecha = binding.etFechaNacimiento.text?.toString().orEmpty().trim()
        if (!validate(nombre, apellidos, celular, fecha)) return

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Snackbar.make(binding.root, "Sesión no válida, vuelve a iniciar sesión", Snackbar.LENGTH_LONG).show()
            return
        }
        val profile = UserProfile(
            id = uid,
            firstName = nombre,
            lastName = apellidos,
            phone = celular,
            birthDate = fecha,
            email = auth.currentUser?.email.orEmpty()
        )
        viewModel.saveProfile(profile)
    }

    private fun validate(nombre: String, apellidos: String, celular: String, fecha: String): Boolean {
        var valid = true
        if (nombre.isEmpty()) {
            binding.tilNombre.error = "Ingresa tu nombre"; valid = false
        } else binding.tilNombre.error = null
        if (apellidos.isEmpty()) {
            binding.tilApellidos.error = "Ingresa tus apellidos"; valid = false
        } else binding.tilApellidos.error = null
        if (celular.length < 10) {
            binding.tilCelular.error = "Ingresa un celular válido"; valid = false
        } else binding.tilCelular.error = null
        if (fecha.isEmpty()) {
            binding.tilFechaNacimiento.error = "Selecciona tu fecha de nacimiento"; valid = false
        } else binding.tilFechaNacimiento.error = null
        return valid
    }

    private fun observeSave() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> communicator.manageLoader(true)
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            goToHome()
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
