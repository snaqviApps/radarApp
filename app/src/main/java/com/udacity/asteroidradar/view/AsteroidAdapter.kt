package com.udacity.asteroidradar.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R.*
import com.udacity.asteroidradar.database.Asteroid

class AsteroidAdapter:  RecyclerView.Adapter<AsteroidAdapter.AsteroidViewHolder>() {

    var data = listOf<Asteroid>()
    set(value){
        field = value
        notifyDataSetChanged()      // to let adapter know, if data has changed
    }
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(layout.main_asteroid_item_list, parent, false)
        return AsteroidViewHolder(view)

    }


    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = data[position]
        val res = holder.itemView.context
        holder.bind(item)
    }



    class AsteroidViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val asteroidCodeName: TextView = itemView.findViewById(id.tv_asteroidCodename)
        val asteroidName2nd: TextView = itemView.findViewById(id.tv_asteroidName2nd)
        var statusImage: ImageView = itemView.findViewById(id.img_Status)

        fun bind(item: Asteroid) {              // added as 'extension function for, anoter
            asteroidCodeName.text = item.codename
            asteroidName2nd.text = item.closeApproachDate
            statusImage.setImageResource(drawable.asteroid_hazardous)
        }
    }
}