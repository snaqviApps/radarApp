package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.main.RadarApiStatus

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("asteroidId")
fun bindTextViewAsteroidId(textView: TextView, asteroid: Asteroid) {
    textView.text = asteroid.asteroidId.toString()
}

/**
 * Using binding-adapter-method as Extension function to TextView
 */
@BindingAdapter("asteroidApproachDate")
fun TextView.bindCloseApproachDate(asteroid: Asteroid) {
    asteroid.let {
        text = it.closeApproachDate
    }
}

@BindingAdapter("dayPictureUrl")
fun bindDayImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUrl = imgUrl.toUri().buildUpon().scheme("https").build()
        Picasso.with(imgView.context)
            .load(imgUrl)
            .into(imgView)
    }
}

@BindingAdapter("asteroidsApiStatus")
fun bindStatus(statusImageView: ImageView, status: RadarApiStatus?) {
    when (status) {
        RadarApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        RadarApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        RadarApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

