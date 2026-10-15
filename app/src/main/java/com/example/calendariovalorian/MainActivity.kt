package com.example.calendariovalorian

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendariovalorian.data.AppDatabase
import com.example.calendariovalorian.data.CeldaDia
import com.example.calendariovalorian.data.InfoDia
import com.example.calendariovalorian.data.TipoTurno
import com.example.calendariovalorian.data.TurnoCalendario
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var rvCalendario: RecyclerView
    private lateinit var tvMesActual: TextView
    private lateinit var btnPintar: Button
    private lateinit var db: AppDatabase

    private lateinit var mesActual: YearMonth
    private var isModoPintar = false
    private var turnoPincel: TipoTurno? = null

    private var turnosGuardados = mutableMapOf<String, InfoDia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mesActual = YearMonth.now()
        db = AppDatabase.getDatabase(this)

        val btnResumen = findViewById<Button>(R.id.btnResumen)
        btnResumen.setOnClickListener { mostrarResumenMensual() }

        tvMesActual = findViewById(R.id.tvMesActual)
        rvCalendario = findViewById(R.id.rvCalendario)
        rvCalendario.layoutManager = GridLayoutManager(this, 7)

        val btnMesAnterior = findViewById<ImageButton>(R.id.btnMesAnterior)
        val btnMesSiguiente = findViewById<ImageButton>(R.id.btnMesSiguiente)
        val btnTurnos = findViewById<Button>(R.id.btnTurnos)
        btnPintar = findViewById(R.id.btnPintar)

        btnTurnos.setOnClickListener {
            startActivity(Intent(this, TurnosActivity::class.java))
        }

        btnPintar.setOnClickListener {
            if (!isModoPintar) {
                mostrarListaTurnosParaElegir { turnoElegido ->
                    turnoPincel = turnoElegido
                    isModoPintar = true
                    try {
                        btnPintar.setBackgroundColor(Color.parseColor(turnoElegido.colorHex))
                    } catch (e: Exception) {
                        btnPintar.setBackgroundColor(Color.parseColor("#33404F"))
                    }
                    btnPintar.setTextColor(Color.WHITE)
                    btnPintar.text = "🖌️ PINTANDO: ${turnoElegido.nombre.uppercase()}"
                }
            } else {
                isModoPintar = false
                turnoPincel = null
                btnPintar.setBackgroundColor(Color.TRANSPARENT)
                btnPintar.setTextColor(Color.parseColor("#1A1E36"))
                btnPintar.text = "PINTAR"
            }
        }

        btnMesAnterior.setOnClickListener {
            mesActual = mesActual.minusMonths(1)
            actualizarCalendario()
        }

        btnMesSiguiente.setOnClickListener {
            mesActual = mesActual.plusMonths(1)
            actualizarCalendario()
        }

        // AL ABRIR LA APP, CARGAMOS LOS DATOS GUARDADOS EN MEMORIA PERMANENTE
        cargarDatosDeLaBaseDeDatos()
    }

    // --- FUNCIÓN QUE RESCATA LOS DÍAS PINTADOS AL INICIAR ---
    // --- FUNCIÓN QUE RESCATA LOS DÍAS Y PRECARGA TURNOS ---
    private fun cargarDatosDeLaBaseDeDatos() {
        lifecycleScope.launch {
            // Comprobamos si es la primera vez que se abre la app
            val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
            val esPrimeraVez = prefs.getBoolean("primeraVez", true)

            if (esPrimeraVez) {
                // Inyectamos el pack básico de turnos
                val turnosPorDefecto = listOf(
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Mañana", abreviatura = "M", colorHex = "#FFEB3B", horaInicio = "08:15", horaFin = "15:15", descansoMinutos = 15),
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Tarde", abreviatura = "T", colorHex = "#FF9800", horaInicio = "15:00", horaFin = "22:00", descansoMinutos = 15),
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Horario Ampliado Mañana", abreviatura = "HAM", colorHex = "#CDDC39", horaInicio = "08:00", horaFin = "16:00", descansoMinutos = 15),
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Horario Ampliado Tarde", abreviatura = "HAT", colorHex = "#FFC107", horaInicio = "14:00", horaFin = "22:00", descansoMinutos = 15),
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Finde de Calidad", abreviatura = "SD", colorHex = "#9C27B0", horaInicio = "", horaFin = "", descansoMinutos = 0),
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Vacaciones", abreviatura = "V", colorHex = "#4CAF50", horaInicio = "", horaFin = "", descansoMinutos = 0),
                    com.example.calendariovalorian.data.TipoTurno(nombre = "Día de Libranza", abreviatura = "L", colorHex = "#9E9E9E", horaInicio = "", horaFin = "", descansoMinutos = 0)
                )

                for (turno in turnosPorDefecto) {
                    db.turnoDao().insertarTipoTurno(turno)
                }

                // Marcamos que ya no es la primera vez para que no se vuelvan a inyectar nunca
                prefs.edit().putBoolean("primeraVez", false).apply()
            }

            // Cargamos los datos normalmente
            val listaTipos = db.turnoDao().obtenerTodosLosTiposTurno()
            val mapaTipos = listaTipos.associateBy { it.id }
            val diasGuardados = db.turnoDao().obtenerTodosLosDiasGuardados()

            turnosGuardados.clear()
            for (dia in diasGuardados) {
                val info = InfoDia(
                    turno1 = mapaTipos[dia.idTurno1],
                    turno2 = mapaTipos[dia.idTurno2],
                    nota = dia.nota
                )
                turnosGuardados[dia.fecha] = info
            }
            actualizarCalendario()
        }
    }

    private fun actualizarCalendario() {
        val hoy = LocalDate.now() // Preguntamos qué día es hoy
        val nombreMes = mesActual.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()
        tvMesActual.text = "$nombreMes ${mesActual.year}"

        val listaCeldas = mutableListOf<CeldaDia>()
        val mesAnterior = mesActual.minusMonths(1)
        val mesSiguiente = mesActual.plusMonths(1)

        val primerDiaMes = mesActual.atDay(1)
        val diaSemanaPrimero = primerDiaMes.dayOfWeek.value
        val diasMesAnterior = mesAnterior.lengthOfMonth()

        // 1. Rellenar con días del mes anterior y buscar sus turnos
        for (i in (diasMesAnterior - (diaSemanaPrimero - 2))..diasMesAnterior) {
            if (diaSemanaPrimero > 1) {
                val fechaClave = "${mesAnterior.year}-${mesAnterior.monthValue}-$i"
                val esHoy = (hoy.year == mesAnterior.year && hoy.monthValue == mesAnterior.monthValue && hoy.dayOfMonth == i)
                listaCeldas.add(CeldaDia(i.toString(), mesAnterior, false, turnosGuardados[fechaClave], esHoy))
            }
        }

        // 2. Rellenar con los días del mes actual
        for (i in 1..mesActual.lengthOfMonth()) {
            val fechaClave = "${mesActual.year}-${mesActual.monthValue}-$i"
            val esHoy = (hoy.year == mesActual.year && hoy.monthValue == mesActual.monthValue && hoy.dayOfMonth == i)
            listaCeldas.add(CeldaDia(i.toString(), mesActual, true, turnosGuardados[fechaClave], esHoy))
        }

        // 3. Rellenar con los días del mes siguiente (Forzamos SIEMPRE 42 celdas)
        val totalCeldas = 42
        var diaSiguiente = 1
        while (listaCeldas.size < totalCeldas) {
            val fechaClave = "${mesSiguiente.year}-${mesSiguiente.monthValue}-$diaSiguiente"
            val esHoy = (hoy.year == mesSiguiente.year && hoy.monthValue == mesSiguiente.monthValue && hoy.dayOfMonth == diaSiguiente)
            listaCeldas.add(CeldaDia(diaSiguiente.toString(), mesSiguiente, false, turnosGuardados[fechaClave], esHoy))
            diaSiguiente++
        }

        val adaptador = CalendarioAdapter(listaCeldas) { celdaClicada ->
            if (isModoPintar && turnoPincel != null) {
                val fechaClave = "${celdaClicada.yearMonth.year}-${celdaClicada.yearMonth.monthValue}-${celdaClicada.numero}"
                val infoActual = turnosGuardados[fechaClave] ?: InfoDia()

                lifecycleScope.launch {
                    // Si tocamos el mismo turno, lo borramos (Goma de borrar)
                    if (infoActual.turno1?.nombre == turnoPincel!!.nombre && infoActual.turno2 == null && infoActual.nota.isEmpty()) {
                        turnosGuardados.remove(fechaClave)
                        db.turnoDao().borrarDia(fechaClave)
                    } else {
                        infoActual.turno1 = turnoPincel
                        turnosGuardados[fechaClave] = infoActual
                        db.turnoDao().guardarTurnoDia(
                            TurnoCalendario(
                                fecha = fechaClave,
                                idTurno1 = infoActual.turno1?.id,
                                idTurno2 = infoActual.turno2?.id,
                                nota = infoActual.nota
                            )
                        )
                    }
                    actualizarCalendario()
                }
            } else {
                abrirSelectorDeTurnos(celdaClicada)
            }
        }
        rvCalendario.adapter = adaptador
    }

    private fun abrirSelectorDeTurnos(celda: CeldaDia) {
        val fechaClave = "${celda.yearMonth.year}-${celda.yearMonth.monthValue}-${celda.numero}"
        val infoActual = turnosGuardados[fechaClave] ?: InfoDia()

        val dialogView = layoutInflater.inflate(R.layout.dialog_opciones_dia, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloOpciones)
        val btnTurno1 = dialogView.findViewById<Button>(R.id.btnElegirTurno1)
        val btnTurno2 = dialogView.findViewById<Button>(R.id.btnElegirTurno2)
        val etNota = dialogView.findViewById<EditText>(R.id.etNotaDia)

        // El botón de borrar que añadimos en el paso anterior
        val btnBorrar = dialogView.findViewById<Button>(R.id.btnBorrarDia)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardarOpciones)

        tvTitulo.text = "Día ${celda.numero} de ${celda.yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).uppercase()}"

        btnTurno1.text = infoActual.turno1?.nombre ?: "SELECCIONAR TURNO 1"
        btnTurno2.text = infoActual.turno2?.nombre ?: "SELECCIONAR TURNO 2"
        etNota.setText(infoActual.nota)

        btnTurno1.setOnClickListener {
            mostrarListaTurnosParaElegir { turnoElegido ->
                infoActual.turno1 = turnoElegido
                btnTurno1.text = turnoElegido.nombre
            }
        }

        btnTurno2.setOnClickListener {
            mostrarListaTurnosParaElegir { turnoElegido ->
                infoActual.turno2 = turnoElegido
                btnTurno2.text = turnoElegido.nombre
            }
        }

        btnBorrar.setOnClickListener {
            turnosGuardados.remove(fechaClave)
            lifecycleScope.launch {
                db.turnoDao().borrarDia(fechaClave) // Borra de la BD
                dialog.dismiss()
                actualizarCalendario()
            }
        }

        btnGuardar.setOnClickListener {
            infoActual.nota = etNota.text.toString()
            turnosGuardados[fechaClave] = infoActual

            lifecycleScope.launch {
                db.turnoDao().guardarTurnoDia(
                    TurnoCalendario(
                        fecha = fechaClave,
                        idTurno1 = infoActual.turno1?.id,
                        idTurno2 = infoActual.turno2?.id,
                        nota = infoActual.nota
                    )
                ) // Guarda en la BD
                dialog.dismiss()
                actualizarCalendario()
            }
        }

        dialog.show()
    }

    private fun mostrarListaTurnosParaElegir(onTurnoElegido: (TipoTurno) -> Unit) {
        lifecycleScope.launch {
            val listaTurnos = db.turnoDao().obtenerTodosLosTiposTurno()
            val dialogView = layoutInflater.inflate(R.layout.dialog_selector_turnos, null)
            val rvTurnosDialog = dialogView.findViewById<RecyclerView>(R.id.rvTurnosDialog)
            rvTurnosDialog.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)

            val builder = android.app.AlertDialog.Builder(this@MainActivity)
            builder.setView(dialogView)
            val dialog = builder.create()

            val adapter = TurnosDialogAdapter(listaTurnos) { turnoElegido ->
                onTurnoElegido(turnoElegido)
                dialog.dismiss()
            }
            rvTurnosDialog.adapter = adapter
            dialog.show()
        }
    }

    private fun mostrarResumenMensual() {
        val conteoMensual = mutableMapOf<String, Int>()
        var horasMensuales = 0.0
        val conteoAnual = mutableMapOf<String, Int>()
        var horasAnuales = 0.0

        for ((fechaStr, infoDia) in turnosGuardados) {
            val partes = fechaStr.split("-")
            if (partes.size != 3) continue
            val year = partes[0].toInt()
            val month = partes[1].toInt()

            val esEsteMes = (year == mesActual.year && month == mesActual.monthValue)
            val esEsteAnual = (year == mesActual.year)

            fun procesarTurno(turno: TipoTurno?) {
                if (turno != null) {
                    val horasTurno = calcularHoras(turno.horaInicio, turno.horaFin, turno.descansoMinutos)

                    if (esEsteMes) {
                        conteoMensual[turno.nombre] = conteoMensual.getOrDefault(turno.nombre, 0) + 1
                        horasMensuales += horasTurno
                    }
                    if (esEsteAnual) {
                        conteoAnual[turno.nombre] = conteoAnual.getOrDefault(turno.nombre, 0) + 1
                        horasAnuales += horasTurno
                    }
                }
            }

            procesarTurno(infoDia.turno1)
            procesarTurno(infoDia.turno2)
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_resumen, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloResumen)
        val layoutMensual = dialogView.findViewById<android.widget.LinearLayout>(R.id.layoutConteosMensuales)
        val tvHorasMensuales = dialogView.findViewById<TextView>(R.id.tvHorasMensuales)
        val layoutAnual = dialogView.findViewById<android.widget.LinearLayout>(R.id.layoutConteosAnuales)
        val tvHorasAnuales = dialogView.findViewById<TextView>(R.id.tvHorasAnuales)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrarResumen)

        tvTitulo.text = "RESUMEN DE ${mesActual.year}"

        layoutMensual.removeAllViews()
        for ((nombre, cantidad) in conteoMensual) {
            val tv = TextView(this)
            tv.text = "• $nombre: $cantidad"
            tv.textSize = 15f
            layoutMensual.addView(tv)
        }
        tvHorasMensuales.text = "⏱️ Total Horas: ${String.format(Locale.US, "%.2f", horasMensuales)}h"

        layoutAnual.removeAllViews()
        for ((nombre, cantidad) in conteoAnual) {
            val tv = TextView(this)
            tv.text = "• $nombre: $cantidad"
            tv.textSize = 15f
            layoutAnual.addView(tv)
        }
        tvHorasAnuales.text = "⏱️ Total Horas: ${String.format(Locale.US, "%.2f", horasAnuales)}h"

        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun calcularHoras(horaInicio: String?, horaFin: String?, descansoMinutos: Int): Double {
        if (horaInicio.isNullOrEmpty() || horaFin.isNullOrEmpty()) return 0.0
        try {
            val partesInicio = horaInicio.split(":")
            val partesFin = horaFin.split(":")
            if (partesInicio.size == 2 && partesFin.size == 2) {
                val minutosInicio = partesInicio[0].toInt() * 60 + partesInicio[1].toInt()
                var minutosFin = partesFin[0].toInt() * 60 + partesFin[1].toInt()

                if (minutosFin < minutosInicio) {
                    minutosFin += 24 * 60
                }

                val minutosEfectivos = (minutosFin - minutosInicio) - descansoMinutos
                return if (minutosEfectivos > 0) minutosEfectivos / 60.0 else 0.0
            }
        } catch (e: Exception) { }
        return 0.0
    }
}