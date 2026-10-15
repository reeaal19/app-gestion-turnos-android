package com.example.calendariovalorian

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendariovalorian.data.TipoTurno

// Este adaptador dibuja la lista con colores dentro de la ventana emergente
class TurnosDialogAdapter(
    private val turnos: List<TipoTurno>,
    private val onTurnoClick: (TipoTurno) -> Unit
) : RecyclerView.Adapter<TurnosDialogAdapter.TurnoViewHolder>() {

    class TurnoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLetraColor: TextView = view.findViewById(R.id.tvLetraColor)
        val tvNombreTurno: TextView = view.findViewById(R.id.tvNombreTurno)
        val tvHorarioTurno: TextView = view.findViewById(R.id.tvHorarioTurno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnoViewHolder {
        // Usamos el MISMO diseño que ya tienes en la pantalla de Turnos Disponibles
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_turno_lista, parent, false)
        return TurnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TurnoViewHolder, position: Int) {
        val turno = turnos[position]
        holder.tvLetraColor.text = turno.abreviatura
        holder.tvNombreTurno.text = turno.nombre
        holder.tvHorarioTurno.text = "(${turno.horaInicio ?: ""} - ${turno.horaFin ?: ""})"

        try {
            holder.tvLetraColor.setBackgroundColor(Color.parseColor(turno.colorHex))
        } catch (e: Exception) {
            // Si el color falla, se queda por defecto
        }

        // Al tocar un turno, lo devuelve a la actividad
        holder.itemView.setOnClickListener {
            onTurnoClick(turno)
        }
    }

    override fun getItemCount(): Int = turnos.size
}