package com.example.calendariovalorian.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
@JvmSuppressWildcards
interface TurnoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTipoTurno(tipoTurno: TipoTurno): Long

    @androidx.room.Update
    suspend fun actualizarTipoTurno(turno: TipoTurno)

    @androidx.room.Delete
    suspend fun borrarTipoTurno(turno: TipoTurno)

    @Query("SELECT * FROM tabla_tipos_turno")
    suspend fun obtenerTodosLosTiposTurno(): List<TipoTurno>

    @Query("SELECT * FROM TurnoCalendario WHERE fecha LIKE :anioMes || '%'")
    suspend fun obtenerTurnosDelMes(anioMes: String): List<TurnoCalendario>

    @Query("DELETE FROM TurnoCalendario WHERE fecha = :fecha")
    suspend fun borrarTurnoDia(fecha: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTurnoCalendario(turno: TurnoCalendario): Long

    @Query("SELECT * FROM TurnoCalendario")
    suspend fun obtenerTurnosCalendario(): List<TurnoCalendario>
    // --- NUEVAS CONSULTAS PARA EL CALENDARIO ---

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun guardarTurnoDia(turno: TurnoCalendario)

    @androidx.room.Query("SELECT * FROM TurnoCalendario")
    suspend fun obtenerTodosLosDiasGuardados(): List<TurnoCalendario>

    @androidx.room.Query("DELETE FROM TurnoCalendario WHERE fecha = :fechaStr")
    suspend fun borrarDia(fechaStr: String)
}