package fr.epf.foodlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "times")
data class Time (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val heure : Int,
    val min : Int
)
