package com.example.gamevault.home.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gamevault.R
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.FragmentCommunicator
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.repositories.UserRepository
import com.example.gamevault.databinding.FragmentGamesBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GamesFragment : Fragment() {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GamesViewModel>()
    private lateinit var communicator: FragmentCommunicator
    private val adapter = GamesAdapter { game ->
        val args = Bundle().apply { putInt("gameId", game.id) }
        findNavController().navigate(R.id.action_games_to_detail, args)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGames.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        // Buscador: cada cambio de texto alimenta el debounce del ViewModel.
        binding.etSearch.addTextChangedListener { text ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }

        loadGreeting()
        observeGames()
    }

    /** Saludo con el nombre del usuario leído de Firestore. */
    private fun loadGreeting() {
        val uid = AuthRepository().currentUser?.uid ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            val result = UserRepository().getUserInfo(uid)
            if (result is ResponseService.Success) {
                val firstName = result.data.firstName.ifEmpty { "gamer" }
                binding.tvUserName.text = firstName
                binding.imgAvatar.text = firstName.take(1).uppercase()
            }
        }
    }

    private fun observeGames() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gamesState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> binding.swipeRefresh.isRefreshing = true
                        is ResponseService.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                            adapter.submitList(state.data)
                            binding.tvEmpty.visibility =
                                if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        }
                        is ResponseService.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
