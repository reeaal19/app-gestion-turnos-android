package com.example.calendariovalorian.data

import java.time.YearMonth

data class InfoDia(
    var turno1: TipoTurno? = null,
    var turno2: TipoTurno? = null,
    var nota: String = ""
)

data class CeldaDia(
    val numero: String,
    val yearMonth: YearMonth,
    val esDelMesActual: Boolean,
    var infoDia: InfoDia? = null,
    val esHoy: Boolean = false
)
