package com.udacity.asteroidradar.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R.*
import com.udacity.asteroidradar.database.Asteroid

class AsteroidAdapter: ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffUtilCallbacks()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = getItem(position)
        val res = holder.itemView.context
        holder.bind(item)
    }

    class AsteroidViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        val asteroidCodeName: TextView = itemView.findViewById(id.tv_asteroidCodename)
        val asteroidName2nd: TextView = itemView.findViewById(id.tv_asteroidName2nd)
        var statusImage: ImageView = itemView.findViewById(id.img_Status)

        fun bind(item: Asteroid) {              // added as 'extension function for, anoter
            asteroidCodeName.text = item.codename
            asteroidName2nd.text = item.closeApproachDate
            statusImage.setImageResource(drawable.asteroid_hazardous)
        }

        companion object {
            fun from(parent: ViewGroup): AsteroidViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(layout.main_asteroid_item_list, parent, false)
                return AsteroidViewHolder(view)
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