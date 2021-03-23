package com.udacity.asteroidradar.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.databinding.MainAsteroidItemListBinding

class AsteroidAdapter(val clickListener: AsteroidListener): ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffUtilCallbacks()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item)
        holder.bind(getItem(position)!!, clickListener)  /** letting data-binding know about clickListener, by adding to 'each' view-holder */

    }

    // 3. TODO_ : Refactor and rename the ViewHolder class’s constructor parameter to take a MainAsteroidItemListBinding
    /**
     * @param MainAsteroidItemListBinding
     */
    class AsteroidViewHolder private constructor(
        private val binding: MainAsteroidItemListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Asteroid, clickListener: AsteroidListener) {
            binding.asteroid = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        // 1. TODO_ : In the companion object, Replace LayoutInflater with MainAsteroidItemListBinding
        companion object {
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

class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit){
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}