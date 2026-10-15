package com.example.calendariovalorian

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendariovalorian.data.CeldaDia

class CalendarioAdapter(
    private val celdas: List<CeldaDia>,
    private val onDiaClick: (CeldaDia) -> Unit
) : RecyclerView.Adapter<CalendarioAdapter.DiaViewHolder>() {

    class DiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumeroDia: TextView = view.findViewById(R.id.tvNumeroDia)
        val tvLetraTurno: TextView = view.findViewById(R.id.tvLetraTurno)
        val tvNotaDia: TextView = view.findViewById(R.id.tvNotaDia)
        val fondoTurno1: View = view.findViewById(R.id.fondoTurno1)
        val fondoTurno2: View = view.findViewById(R.id.fondoTurno2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dia_calendario, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = parent.measuredHeight / 6
        view.layoutParams = layoutParams
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val celda = celdas[position]
        holder.tvNumeroDia.text = celda.numero

        // 1. Colores base, Sombreado y DÍA ACTUAL (HOY)
        if (celda.esHoy) {
            // SI ES HOY: Número en rojo oscuro, negrita y un pelín más grande
            holder.tvNumeroDia.setTextColor(Color.parseColor("#E64A19"))
            holder.tvNumeroDia.setTypeface(null, Typeface.BOLD)
            holder.tvNumeroDia.textSize = 14f
            holder.itemView.alpha = 1.0f
            holder.itemView.setBackgroundColor(Color.parseColor("#E1E6ED"))
        } else {
            // SI NO ES HOY: Vuelve al texto normal
            holder.tvNumeroDia.setTypeface(null, Typeface.NORMAL)
            holder.tvNumeroDia.textSize = 12f

            if (!celda.esDelMesActual) {
                holder.itemView.setBackgroundColor(Color.parseColor("#546170"))
                holder.tvNumeroDia.setTextColor(Color.parseColor("#B0BEC5"))
                holder.itemView.alpha = 0.4f // Efecto difuminado (sombra)
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#E1E6ED"))
                holder.tvNumeroDia.setTextColor(Color.parseColor("#333333"))
                holder.itemView.alpha = 1.0f
            }
        }

        // 2. Resetear valores previos
        holder.tvLetraTurno.text = ""
        holder.tvNotaDia.text = ""
        holder.fondoTurno1.setBackgroundColor(Color.TRANSPARENT)
        holder.fondoTurno2.setBackgroundColor(Color.TRANSPARENT)

        // 3. Pintar turnos
        celda.infoDia?.let { info ->
            val abreviaturas = mutableListOf<String>()
            if (info.turno1 != null) abreviaturas.add(info.turno1!!.abreviatura)
            if (info.turno2 != null) abreviaturas.add(info.turno2!!.abreviatura)

            holder.tvLetraTurno.text = abreviaturas.joinToString("/")
            holder.tvNotaDia.text = info.nota

            try {
                if (info.turno1 != null && info.turno2 != null) {
                    holder.fondoTurno1.setBackgroundColor(Color.parseColor(info.turno1!!.colorHex))
                    holder.fondoTurno2.setBackgroundColor(Color.parseColor(info.turno2!!.colorHex))
                } else if (info.turno1 != null) {
                    holder.fondoTurno1.setBackgroundColor(Color.parseColor(info.turno1!!.colorHex))
                    holder.fondoTurno2.setBackgroundColor(Color.parseColor(info.turno1!!.colorHex))
                }
            } catch (e: Exception) {}
        }

        holder.itemView.setOnClickListener {
            // ¡Esta línea hace que el móvil dé una micro-vibración elegante al tocar!
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)

            if (celda.numero.isNotEmpty()) {
                onDiaClick(celda)
            }
        }
    } // <--- ESTA ES LA LLAVE QUE TE FALTABA

    override fun getItemCount(): Int = celdas.size
}