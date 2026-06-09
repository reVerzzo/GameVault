package com.example.gamevault.home.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamevault.R
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.FavoriteGame
import com.example.gamevault.databinding.FragmentFavoritesBinding
import com.example.gamevault.home.HomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FavoritesViewModel>()

    private var allFavorites: List<FavoriteGame> = emptyList()
    private val adapter = FavoritesAdapter(
        onItemClick = { favorite ->
            val args = Bundle().apply { putInt("gameId", favorite.gameId) }
            findNavController().navigate(R.id.action_favorites_to_detail, args)
        },
        onRemoveClick = { favorite -> confirmRemove(favorite) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvWishlist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWishlist.adapter = adapter

        binding.etSearch.addTextChangedListener { text -> filter(text?.toString().orEmpty()) }
        binding.btnExplorar.setOnClickListener {
            (requireActivity() as HomeActivity).goToTab(R.id.gamesFragment)
        }

        observeFavorites()
        viewModel.loadFavorites()
    }

    /** Filtra la lista local del usuario por nombre. */
    private fun filter(query: String) {
        val filtered = if (query.isBlank()) {
            allFavorites
        } else {
            allFavorites.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
        adapter.submitList(filtered)
    }

    private fun confirmRemove(favorite: FavoriteGame) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Quitar de tu lista")
            .setMessage("¿Eliminar \"${favorite.name}\" de tus favoritos?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.removeFavorite(favorite.gameId)
                Snackbar.make(binding.root, "Eliminado de tu lista", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> render(state.data)
                        is ResponseService.Error ->
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun render(favorites: List<FavoriteGame>) {
        allFavorites = favorites
        val isEmpty = favorites.isEmpty()
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvWishlist.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.tvCount.text = resources.getQuantityString(
            R.plurals.favorites_count, favorites.size, favorites.size
        )
        filter(binding.etSearch.text?.toString().orEmpty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
