package com.example.calendariovalorian.data

object TurnoUtils {
    fun getTurnosPorDefecto(): List<TipoTurno> {
        return listOf(
            TipoTurno(nombre = "Mañana", abreviatura = "M", colorHex = "#FFEB3B", horaInicio = "08:15", horaFin = "15:15"),
            TipoTurno(nombre = "Tarde", abreviatura = "T", colorHex = "#FF9800", horaInicio = "15:00", horaFin = "22:00"),
            TipoTurno(nombre = "Horario Ampliado Mañana", abreviatura = "HAM", colorHex = "#CDDC39", horaInicio = "08:00", horaFin = "16:00"),
            TipoTurno(nombre = "Horario Ampliado Tarde", abreviatura = "HAT", colorHex = "#FFC107", horaInicio = "14:00", horaFin = "22:00"),
            TipoTurno(nombre = "Festivo Trabajado Mañana", abreviatura = "FM", colorHex = "#00A3E0", horaInicio = "09:00", horaFin = "15:00"),
            TipoTurno(nombre = "Festivo Trabajado Tarde", abreviatura = "FT", colorHex = "#1A1E36", horaInicio = "15:00", horaFin = "21:00"),
            TipoTurno(nombre = "Finde de Calidad", abreviatura = "SD", colorHex = "#9C27B0", horaInicio = "Libre", horaFin = "Calidad"),
            TipoTurno(nombre = "Vacaciones", abreviatura = "V", colorHex = "#4CAF50", horaInicio = "Descanso", horaFin = "Anual"),
            TipoTurno(nombre = "Permiso", abreviatura = "P", colorHex = "#00BCD4", horaInicio = "Retribuido", horaFin = ""),
            TipoTurno(nombre = "Baja Laboral / IT", abreviatura = "IT", colorHex = "#F44336", horaInicio = "Médica", horaFin = ""),
            TipoTurno(nombre = "Día de Cierre de Tienda", abreviatura = "CT", colorHex = "#607D8B", horaInicio = "Cierre", horaFin = ""),
            TipoTurno(nombre = "Día de Libranza", abreviatura = "L", colorHex = "#E0E0E0", horaInicio = "Libre", horaFin = "")
        )
    }
}
