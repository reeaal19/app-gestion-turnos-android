package com.example.calendariovalorian.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_tipos_turno")
data class TipoTurno(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val abreviatura: String,
    val colorHex: String,
    val horaInicio: String?,
    val horaFin: String?,
    val descansoMinutos: Int = 0 // NUEVO: Tiempo de descanso en minutos
)