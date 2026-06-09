package com.example.gamevault.home.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamevault.R
import com.example.gamevault.core.model.FavoriteGame
import com.example.gamevault.databinding.ItemFavoriteBinding

/**
 * Adapter de la lista de favoritos. Permite abrir el detalle (click en
 * la card) y eliminar el favorito (botón rojo).
 */
class FavoritesAdapter(
    private val onItemClick: (FavoriteGame) -> Unit = {},
    private val onRemoveClick: (FavoriteGame) -> Unit = {}
) : ListAdapter<FavoriteGame, FavoritesAdapter.FavoriteViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteGame) {
            val context = binding.root.context
            binding.tvTitle.text = favorite.name
            binding.tvMeta.text = context.getString(
                R.string.favorite_meta_format, favorite.rating, favorite.genres
            )

            bindStatus(favorite.status)

            Glide.with(binding.imgCover)
                .load(favorite.backgroundImage)
                .centerCrop()
                .placeholder(R.color.surface_muted)
                .into(binding.imgCover)

            binding.root.setOnClickListener { onItemClick(favorite) }
            binding.btnRemove.setOnClickListener { onRemoveClick(favorite) }
        }

        private fun bindStatus(status: String) {
            val (bg, label) = when (status) {
                "JUGANDO" -> R.drawable.bg_status_jugando to "Jugando"
                "COMPLETADO" -> R.drawable.bg_status_completado to "Completado"
                else -> R.drawable.bg_status_por_jugar to "Por jugar"
            }
            binding.chipStatus.setBackgroundResource(bg)
            binding.chipStatus.text = label
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FavoriteGame>() {
            override fun areItemsTheSame(oldItem: FavoriteGame, newItem: FavoriteGame) =
                oldItem.gameId == newItem.gameId

            override fun areContentsTheSame(oldItem: FavoriteGame, newItem: FavoriteGame) =
                oldItem == newItem
        }
    }
}
