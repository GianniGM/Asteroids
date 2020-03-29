package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

/**
 * Adapter for the recycler view of [MainFragment]
 */
class AsteroidsAdapter(private val clickListener: AsteroidClickListner) : ListAdapter<Asteroid, AsteroidsAdapter.AsteroidViewHolder>(Companion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.createBinding(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.bindView(getItem(position), clickListener)
    }

    companion object : DiffUtil.ItemCallback<Asteroid>(){
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean =
            oldItem.id ==  newItem.id

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean =
            oldItem == newItem
    }

    /**
     * View holder for [AsteroidsAdapter]
     */
    class AsteroidViewHolder private constructor(
        private val binding: AsteroidListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(
            item: Asteroid,
            clickListener: AsteroidClickListner
        ) {
            binding.asteroid = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun createBinding(parent: ViewGroup): AsteroidViewHolder {
                val binding = AsteroidListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AsteroidViewHolder(binding)
            }
        }
    }
}

/**
 * Classe click listener used for databinding
 */
class AsteroidClickListner( val clickListener: (Asteroid) -> Unit){
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}


