package com.example.calendariovalorian

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendariovalorian.data.AppDatabase
import com.example.calendariovalorian.data.TipoTurno
import kotlinx.coroutines.launch

class TurnosActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvListaTurnos: RecyclerView
    private lateinit var adaptador: TurnosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turnos)

        db = AppDatabase.getDatabase(this)
        rvListaTurnos = findViewById(R.id.rvListaTurnos)
        rvListaTurnos.layoutManager = LinearLayoutManager(this)

        val btnCrearTurno = findViewById<Button>(R.id.btnCrearTurno)

        // Iniciamos el adaptador con la lista vacía al principio
        adaptador = TurnosAdapter(emptyList()) { turnoClicado ->
            abrirDialogoCrearEditar(turnoClicado) // Si tocamos un turno, lo pasamos para editar
        }
        rvListaTurnos.adapter = adaptador

        // Botón "CREAR TURNO NUEVO": Abre el menú pasando "null" (vacío)
        btnCrearTurno.setOnClickListener {
            abrirDialogoCrearEditar(null)
        }

        cargarTurnos()
    }

    private fun cargarTurnos() {
        lifecycleScope.launch {
            val lista = db.turnoDao().obtenerTodosLosTiposTurno()
            adaptador.actualizarLista(lista)
        }
    }

    private fun abrirDialogoCrearEditar(turnoAEditar: TipoTurno?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_crear_editar_turno, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloDialogoTurno)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etAbreviatura = dialogView.findViewById<EditText>(R.id.etAbreviatura)
        val etColor = dialogView.findViewById<EditText>(R.id.etColor)
        val etHoraInicio = dialogView.findViewById<EditText>(R.id.etHoraInicio)
        val etHoraFin = dialogView.findViewById<EditText>(R.id.etHoraFin)
        val etDescanso = dialogView.findViewById<EditText>(R.id.etDescanso)

        // Enlazamos los botones de colores
        dialogView.findViewById<View>(R.id.btnColorAmarillo).setOnClickListener { etColor.setText("#FFEB3B") }
        dialogView.findViewById<View>(R.id.btnColorNaranja).setOnClickListener { etColor.setText("#FF9800") }
        dialogView.findViewById<View>(R.id.btnColorVerde).setOnClickListener { etColor.setText("#4CAF50") }
        dialogView.findViewById<View>(R.id.btnColorAzul).setOnClickListener { etColor.setText("#03A9F4") }
        dialogView.findViewById<View>(R.id.btnColorRojo).setOnClickListener { etColor.setText("#F44336") }
        dialogView.findViewById<View>(R.id.btnColorMorado).setOnClickListener { etColor.setText("#9C27B0") }
        dialogView.findViewById<View>(R.id.btnColorGris).setOnClickListener { etColor.setText("#9E9E9E") }
        dialogView.findViewById<View>(R.id.btnColorOscuro).setOnClickListener { etColor.setText("#33404F") }
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardarTurno)
        val btnBorrar = dialogView.findViewById<Button>(R.id.btnBorrarTurno)

        // Si estamos EDITANDO un turno, rellenamos los datos
        if (turnoAEditar != null) {
            tvTitulo.text = "EDITAR TURNO"
            etNombre.setText(turnoAEditar.nombre)
            etAbreviatura.setText(turnoAEditar.abreviatura)
            etColor.setText(turnoAEditar.colorHex)
            etHoraInicio.setText(turnoAEditar.horaInicio)
            etHoraFin.setText(turnoAEditar.horaFin)
            etDescanso.setText(turnoAEditar.descansoMinutos.toString())

            // Hacemos visible el botón de borrar
            btnBorrar.visibility = View.VISIBLE
        }

        btnGuardar.setOnClickListener {
            val descansoStr = etDescanso.text.toString()
            val minutosDescanso = if (descansoStr.isNotEmpty()) descansoStr.toInt() else 0

            val nuevoTurno = TipoTurno(
                id = turnoAEditar?.id ?: 0, // Si estamos editando, mantenemos su ID. Si es nuevo, es 0 (Room lo autogenera)
                nombre = etNombre.text.toString(),
                abreviatura = etAbreviatura.text.toString(),
                colorHex = etColor.text.toString(),
                horaInicio = etHoraInicio.text.toString(),
                horaFin = etHoraFin.text.toString(),
                descansoMinutos = minutosDescanso
            )

            lifecycleScope.launch {
                if (turnoAEditar == null) {
                    db.turnoDao().insertarTipoTurno(nuevoTurno) // Asegúrate de tener un método @Insert en TurnoDao
                } else {
                    db.turnoDao(). actualizarTipoTurno(nuevoTurno)
                }
                dialog.dismiss()
                cargarTurnos() // Recargamos la lista visual
            }
        }

        btnBorrar.setOnClickListener {
            if (turnoAEditar != null) {
                lifecycleScope.launch {
                    db.turnoDao().borrarTipoTurno(turnoAEditar)
                    dialog.dismiss()
                    cargarTurnos()
                }
            }
        }

        dialog.show()
    }
}