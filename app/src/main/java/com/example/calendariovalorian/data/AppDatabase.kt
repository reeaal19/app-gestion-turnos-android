package com.example.calendariovalorian.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ¡OJO AQUÍ! Tienen que estar las dos tablas: TipoTurno y TurnoCalendario. Versión 4.
@Database(entities = [TipoTurno::class, TurnoCalendario::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun turnoDao(): TurnoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendario_turnos_db"
                )
                    .fallbackToDestructiveMigration() // Destruye y recrea si hay cambios para que no explote
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}