package com.udacity.asteroidradar.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "asteroid_info_table")
@Parcelize
data class Asteroid(
        @PrimaryKey(autoGenerate = true)
//        val asteroidId: Long = 0L,
        val asteroidId: Long,

        @ColumnInfo(name = "code_name")
        val codename: String,

        @ColumnInfo(name = "approach_date")
        val closeApproachDate: String,

        @ColumnInfo(name = "absolute_magnitude")
        val absoluteMagnitude: Double,

        @ColumnInfo(name = "estimated_diameter")
        val estimatedDiameter: Double,

        @ColumnInfo(name = "relative_velocity")
        val relativeVelocity: Double,

        @ColumnInfo(name = "distance_from_earth")
        val distanceFromEarth: Double,

        @ColumnInfo(name = "isPotential_hazardous")
        val isPotentiallyHazardous: Boolean)

        : Parcelable