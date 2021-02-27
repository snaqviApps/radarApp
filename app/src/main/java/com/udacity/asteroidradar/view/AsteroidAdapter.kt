package com.udacity.asteroidradar.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.databinding.MainAsteroidItemListBinding

class AsteroidAdapter: ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffUtilCallbacks()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = getItem(position)
        val res = holder.itemView.context
        holder.bind(item)
    }

    // 3. TODO_ : Refactor and rename the ViewHolder class’s constructor parameter to take a ListItemSleepNightBinding
    class AsteroidViewHolder private constructor(
        private val binding: MainAsteroidItemListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Asteroid) {
            binding.asteroid = item
            binding.executePendingBindings()
        }

        // 1. TODO_ : In the companion object, Replace LayoutInflater with ListItemSleepNightBinding
        companion object {

        // 2. TODO_: In the from() function, use ListItemSleepNightBinding.inflate to create a binding object.
            fun from(parent: ViewGroup): AsteroidViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MainAsteroidItemListBinding.inflate(
                        layoutInflater, parent, false)
                return AsteroidViewHolder(binding)
            }
        }
    }

}

class AsteroidDiffUtilCallbacks: DiffUtil.ItemCallback<Asteroid>(){
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.asteroidId == newItem.asteroidId
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}