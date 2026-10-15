package com.example.calendariovalorian.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TurnoCalendario")
data class TurnoCalendario(
    @PrimaryKey val fecha: String,
    val idTurno1: Int? = null,
    val idTurno2: Int? = null,
    val nota: String = ""
)
