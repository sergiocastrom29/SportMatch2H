package com.example.sportmatch2h;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportmatch2h.BottomNavHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PasarelaActivity extends AppCompatActivity {

    private String nombreDeporteElegido;
    private String nombreComplejoElegido;
    private String nombrePistaElegida;
    private String rangoHora;
    private double precioFinal;
    private String fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasarela);
        BottomNavHelper.setup(this, findViewById(android.R.id.content));

        nombreDeporteElegido = getIntent().getStringExtra("DEPORTE_SELECCIONADO");
        nombreComplejoElegido = getIntent().getStringExtra("COMPLEJO_SELECCIONADO");
        nombrePistaElegida = getIntent().getStringExtra("PISTA_SELECCIONADA");
        rangoHora = getIntent().getStringExtra("HORARIO_SELECCIONADO");
        fechaSeleccionada = getIntent().getStringExtra("FECHA_SELECCIONADA");

        String precioStr = getIntent().getStringExtra("PRECIO_PISTA");
        if (precioStr != null) {
            precioStr = precioStr.replace("€", "").replace(",", ".").trim();
            try {
                precioFinal = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                precioFinal = 0.0;
            }
        }

        TextView tvDeporte  = findViewById(R.id.tv_pago_deporte);
        TextView tvComplejo = findViewById(R.id.tv_pago_complejo);
        TextView tvPista    = findViewById(R.id.tv_pago_pista);
        TextView tvHorario  = findViewById(R.id.tv_pago_horario);
        TextView tvPrecio   = findViewById(R.id.tv_pago_precio);

        if (nombreDeporteElegido != null) tvDeporte.setText(nombreDeporteElegido);
        if (nombreComplejoElegido != null) tvComplejo.setText(nombreComplejoElegido);
        if (nombrePistaElegida != null) tvPista.setText(nombrePistaElegida);
        if (rangoHora != null) tvHorario.setText(rangoHora);
        if (precioStr != null) tvPrecio.setText(precioStr + " €");
    }

    public void confirmarReserva(View v) {
        String idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> nuevaReserva = new HashMap<>();
        nuevaReserva.put("confirmado", true);
        nuevaReserva.put("fecha", new Date());
        nuevaReserva.put("fecha_reserva", fechaSeleccionada);
        nuevaReserva.put("hora_reserva", rangoHora);
        nuevaReserva.put("id_instalacion", nombrePistaElegida);
        nuevaReserva.put("id_unico_instalacion", nombreComplejoElegido + "|" + nombrePistaElegida);
        nuevaReserva.put("id_usuario", idUsuarioActual);
        nuevaReserva.put("nombre_complejo", nombreComplejoElegido);
        nuevaReserva.put("deporte", nombreDeporteElegido);
        nuevaReserva.put("total_pagado", precioFinal);

        FirebaseFirestore.getInstance().collection("reservas")
                .add(nuevaReserva)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reserva confirmada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
